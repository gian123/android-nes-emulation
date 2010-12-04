package ui;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

public class glRender implements GLEventListener{

	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL gl = drawable.getGL();   
		gl.glClearColor(0.0f, 0.0f, 0.6f, 1.0f);             // 设置刷新背景色   
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);// 刷新背景   
		gl.glLoadIdentity();       
		play(drawable);   		                  
		gl.glFlush();       
	}

	private void play(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL gl = drawable.getGL();
		gl.glBegin(GL.GL_QUADS);
		gl.glColor3f(0.0f, 1.0f, 0.0f);
		gl.glVertex3d(-1.0f, 1.0f, 0.0f);
		gl.glVertex3d(1.0f, 1.0f, 0.0f);
		gl.glVertex3d(1.0f, -1.0f, 0.0f);
		gl.glVertex3d(-1.0f, -1.0f, 0.0f);
		gl.glEnd();
	}

	@Override
	public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL gl=drawable.getGL();   
		   
		GLU glu=new GLU();   
		                 
		gl.glViewport(0,0,600,480);         // 设置OpenGL视口大小。       
		gl.glMatrixMode(GL.GL_PROJECTION);          // 设置当前矩阵为投影矩阵。   
		gl.glLoadIdentity();                        // 重置当前指定的矩阵为单位矩阵   
		glu.gluPerspective                          // 设置透视图   
		( 45.0f,                            // 透视角设置为 45 度   
		  (float)600/(float)480,    // 窗口的宽与高比   
		  0.1f,                             // 视野透视深度:近点1.0f   
		  100.0f                           // 视野透视深度:始点0.1f远点1000.0f   
		);   
		    // 这和照象机很类似，第一个参数设置镜头广角度，第二个参数是长宽比，后面是远近剪切。   
	    gl.glMatrixMode(GL.GL_MODELVIEW);               // 设置当前矩阵为模型视图矩阵   
		gl.glLoadIdentity();   		                   
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}
	
}
