package net.vektah.codeglance;

/**
 * Created with IntelliJ IDEA.
 * User: adam
 * Date: 1/16/13
 * Time: 7:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class Scale {

    private float vscale;

    public Scale(float vscale) {
        this.vscale = vscale;
    }

    public int charYtoPointY(int y) {
        return (int) (y * vscale);
    }

    public int charXToPointX(int x) {
        return (int) (Math.log(x/10.0+1) * 15.0);
    }

    public void setVScale(float scale) {
        vscale = scale;
    }
}
