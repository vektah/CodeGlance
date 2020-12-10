package net.vektah.codeglance

import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.editor.Editor
import javafx.scene.shape.Shape
import net.vektah.codeglance.config.Config
import net.vektah.codeglance.config.ConfigService
import net.vektah.codeglance.render.ScrollState
import java.awt.*
import java.awt.event.*
import javax.swing.JPanel

class Scrollbar(val editor: Editor, val scrollstate : ScrollState) : JPanel(), MouseListener, MouseWheelListener, MouseMotionListener {
    private var scrollStart: Int = 0
    private var mouseStart: Int = 0
    private val defaultCursor = Cursor(Cursor.DEFAULT_CURSOR)
    private var resizing = false
    private var dragging = false
    private var resizeStart: Int = 0
    private var widthStart: Int = 0
    private val configService = ServiceManager.getService(ConfigService::class.java)
    private var config: Config = configService.state!!
    private var visibleRectColor: Color = Color.decode("#" + config.viewportColor)
    private var bgColor: Color = Color.decode("#" + config.bgColor)
    private var dividerColor: Color = Color.decode("#" + config.dividerColor)
    private var bgColorEnabled = config.bgColorEnabled
    private var dividerColorEnabled = config.dividerColorEnabled
    private var rightAligned = config.isRightAligned
    override fun mouseEntered(e: MouseEvent?) {}
    override fun mouseExited(e: MouseEvent?) {}

    private fun isInReizeGutter(x: Int): Boolean {
        if (config.locked) {
            return false
        }
        if (config.isRightAligned) {
            return x >= 0 && x < 8
        } else {
            return x >= config.width - 8 && x <= config.width
        }
    }

    init {
        configService.onChange {
            config = configService.state!!
            visibleRectColor = Color.decode("#" + config.viewportColor)
        }

        addMouseWheelListener(this)
        addMouseListener(this)
        addMouseMotionListener(this)
    }

    override fun mouseDragged(e: MouseEvent?) {
        if (resizing) {
            config.width = widthStart + if(config.isRightAligned) resizeStart - e!!.xOnScreen else e!!.xOnScreen - resizeStart
            if (config.width < 50) {
                config.width = 50
            } else if (config.width > 250) {
                config.width = 250
            }
            configService.notifyChange()
        }

        if (dragging) {
            // Disable animation when dragging for better experience.
            editor.scrollingModel.disableAnimation()
            scrollTo(e!!.y)
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
            if (config.jumpOnMouseDown) {
                scrollTo(e!!.y)
            }

            scrollStart = editor.scrollingModel.verticalScrollOffset
            mouseStart = e!!.y
        }
    }

    private fun scrollTo(y: Int) {
        val percentage = (y + scrollstate.visibleStart) / scrollstate.documentHeight.toFloat()
        val offset = editor.component.size.height / 2
        editor.scrollingModel.scrollVertically((percentage * editor.contentComponent.size.height - offset).toInt())
    }

    override fun mouseReleased(e: MouseEvent?) {
        dragging = false
        resizing = false
    }

    override fun mouseClicked(e: MouseEvent?) {
        if (!config.jumpOnMouseDown) {
            scrollTo(e!!.y)
        }
    }

    override fun mouseMoved(e: MouseEvent?) {
        if (isInReizeGutter(e!!.x)) {
            cursor = if (config.isRightAligned) Cursor(Cursor.W_RESIZE_CURSOR) else Cursor(Cursor.E_RESIZE_CURSOR)
        } else {
            cursor = defaultCursor
        }
    }

    override fun mouseWheelMoved(mouseWheelEvent: MouseWheelEvent) {
        editor.scrollingModel.scrollVertically(editor.scrollingModel.verticalScrollOffset + (mouseWheelEvent.wheelRotation * editor.lineHeight * 3))
    }

    override fun paint(gfx: Graphics?) {
        val g = gfx as Graphics2D
        val g2 = gfx
        val g3 = gfx

        g.color = visibleRectColor
        g.composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.20f)
        g.fillRect(0, scrollstate.viewportStart - scrollstate.visibleStart, width, scrollstate.viewportHeight)
        if(dividerColorEnabled) {
            g2.color = dividerColor
            if(rightAligned)g2.fillRect(0,0,1,height)
            if(!rightAligned)g2.fillRect(width - 1, 0,1,height)
        }
        g3.color = bgColor
        if(bgColorEnabled)g3.fillRect(0, 0,width, height)
    }
}
