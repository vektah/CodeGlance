package net.vektah.codeglance

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.ScrollType
import net.vektah.codeglance.config.Config
import net.vektah.codeglance.config.ConfigService
import net.vektah.codeglance.render.CoordinateHelper
import java.awt.*
import java.awt.event.*
import javax.swing.JPanel

class Scrollbar(val editor: Editor, val coords : CoordinateHelper) : JPanel(), MouseListener, MouseWheelListener, MouseMotionListener {
    private var scrollStart: Int = 0
    private var mouseStart: Int = 0
    private val defaultCursor = Cursor(Cursor.DEFAULT_CURSOR)
    private val resizeCursor = Cursor(Cursor.W_RESIZE_CURSOR)
    private var resizing = false
    private var dragging = false
    private var resizeStart: Int = 0
    private var widthStart: Int = 0
    private val configService = ServiceManager.getService(ConfigService::class.java)
    private var config: Config = configService.state!!
    private var viewportColor: Color = Color.decode("#" + config.viewportColor)

    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}

    private fun isInReizeGutter(x: Int): Boolean = x >= 0 && x < 8

    init {
        configService.onChange {
            config = configService.state!!
            viewportColor = Color.decode("#" + config.viewportColor)
        }

        addMouseWheelListener(this)
        addMouseListener(this)
        addMouseMotionListener(this)
    }

    override fun mouseDragged(e: MouseEvent?) {
        if (resizing) {
            config.width = widthStart + (resizeStart - e!!.xOnScreen)
            if (config.width < 1) {
                config.width = 1
            }
            configService.notifyChange()
        }

        if (dragging) {
            // Disable animation when dragging for better experience.
            editor.scrollingModel.disableAnimation()

            editor.scrollingModel.scrollVertically(scrollStart + coords.pixelsToLines(e!!.y - mouseStart) * editor.lineHeight)
            editor.scrollingModel.enableAnimation()
        }
    }

    override fun mousePressed(e: MouseEvent?) {
        if (!dragging && isInReizeGutter(e!!.x)) {
            resizing = true
        } else if (!resizing) {
            dragging = true
        }

        if (resizing) {
            resizeStart = e!!.xOnScreen
            widthStart = config.width
        }

        if (dragging) {
            val visibleArea = editor.scrollingModel.visibleArea
            val firstVisibleLine = getMapYFromEditorY(visibleArea.minY.toInt())
            val height = coords.linesToPixels(((visibleArea.maxY - visibleArea.minY) / editor.lineHeight).toInt())

            val panelY = e!!.y - y

            if (config.jumpOnMouseDown && (panelY <= firstVisibleLine || panelY >= (firstVisibleLine + height))) {
                editor.scrollingModel.disableAnimation()
                editor.scrollingModel.scrollTo(editor.offsetToLogicalPosition(coords.screenSpaceToOffset(e.y, config.percentageBasedClick)), ScrollType.CENTER)
                editor.scrollingModel.enableAnimation()
            }

            scrollStart = editor.scrollingModel.verticalScrollOffset
            mouseStart = e.y
        }
    }

    override fun mouseReleased(e: MouseEvent?) {
        dragging = false
        resizing = false
    }

    override fun mouseClicked(e: MouseEvent?) {
        if (!config.jumpOnMouseDown) {
            editor.scrollingModel.scrollTo(editor.offsetToLogicalPosition(coords.screenSpaceToOffset(e!!.y, config.percentageBasedClick)), ScrollType.CENTER)
        }
    }

    override fun mouseMoved(e: MouseEvent?) {
        if (isInReizeGutter(e!!.x)) {
            cursor = resizeCursor
        } else {
            cursor = defaultCursor
        }
    }

    override fun mouseWheelMoved(mouseWheelEvent: MouseWheelEvent) {
        editor.scrollingModel.scrollVertically(editor.scrollingModel.verticalScrollOffset + (mouseWheelEvent.wheelRotation * editor.lineHeight * 3))
    }

    private fun getMapYFromEditorY(y: Int): Int {
        val offset = editor.logicalPositionToOffset(editor.xyToLogicalPosition(Point(0, y)))

        return coords.offsetToScreenSpace(offset)
    }

    override fun paint(gfx: Graphics?) {
        val g = gfx as Graphics2D
        val visibleArea = editor.scrollingModel.visibleArea
        val firstVisibleLine = getMapYFromEditorY(visibleArea.minY.toInt())
        val height = coords.linesToPixels(((visibleArea.maxY - visibleArea.minY) / editor.lineHeight).toInt())

        // Draw the current viewport
        g.color = viewportColor
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.50f)
        g.drawRect(0, firstVisibleLine, width, height)
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f)
        g.fillRect(0, firstVisibleLine, width, height)
    }
}