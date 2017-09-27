package OpenGLES20_furukawa;

/**
 * Created by a50602 on 2017/03/17.
 */

public abstract class GLObject {
    @Override
    protected void finalize() throws Throwable{
        super.finalize();
        dispose();
    }

    public abstract void bind();//バインド
    public abstract void unbind();//アンバインド
    public abstract void dispose();//解放

}
