package ui;

import javax.media.opengl.*;
import javax.media.opengl.glu.GLU;

public class glRender implements GLEventListener{

	@Override
	public void display(GLAutoDrawable drawable) {
		// TODO Auto-generated method stub
		GL gl = drawable.getGL();   
		gl.glClearColor(0.0f, 0.0f, 0.6f, 1.0f);             // ����ˢ�±���ɫ   
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);// ˢ�±���   
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
		                 
		gl.glViewport(0,0,600,480);         // ����OpenGL�ӿڴ�С��       
		gl.glMatrixMode(GL.GL_PROJECTION);          // ���õ�ǰ����ΪͶӰ����   
		gl.glLoadIdentity();                        // ���õ�ǰָ���ľ���Ϊ��λ����   
		glu.gluPerspective                          // ����͸��ͼ   
		( 45.0f,                            // ͸�ӽ�����Ϊ 45 ��   
		  (float)600/(float)480,    // ���ڵĿ���߱�   
		  0.1f,                             // ��Ұ͸�����:����1.0f   
		  100.0f                           // ��Ұ͸�����:ʼ��0.1fԶ��1000.0f   
		);   
		    // �������������ƣ���һ���������þ�ͷ��Ƕȣ��ڶ��������ǳ���ȣ�������Զ�����С�   
	    gl.glMatrixMode(GL.GL_MODELVIEW);               // ���õ�ǰ����Ϊģ����ͼ����   
		gl.glLoadIdentity();   		                   
	}

	@Override
	public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
			int arg4) {
		// TODO Auto-generated method stub
		
	}
	
}
