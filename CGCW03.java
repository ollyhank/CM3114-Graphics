import static com.jogamp.opengl.GL3.*;

import java.io.File;
import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import Basic.ShaderProg;
import Basic.Transform;
import Basic.Vec4;
import Objects.SCube;

import Objects.SObject;
import com.jogamp.nativewindow.WindowClosingProtocol.WindowClosingMode;
;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.util.FPSAnimator;


import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureIO;


public class CGCW03{

	final GLWindow window; //Define a window
	final FPSAnimator animator=new FPSAnimator(60, true);
	final Renderer renderer = new Renderer();

	public CGCW03() {
		GLProfile glp = GLProfile.get(GLProfile.GL3);
		GLCapabilities caps = new GLCapabilities(glp);
		window = GLWindow.create(caps);

		window.addGLEventListener(renderer); //Set the window to listen GLEvents
		animator.add(window);

		window.setDefaultCloseOperation(WindowClosingMode.DISPOSE_ON_CLOSE);
		//The above line will only close the window but not exit the program
		//Add System.exit(0) in dispose() function will cause it exit

		window.setTitle("Coursework 3");
		window.setSize(500,500);
		window.setVisible(true);
		animator.start();
	}

	public static void main(String[] args) {
		new CGCW03();

	}

	class Renderer implements GLEventListener {

		private Transform T = new Transform();

		private Texture texture;

		//VAOs and VBOs parameters
		private int idPoint=0, numVAOs = 1;
		private int idBuffer=0, numVBOs = 1;
		private int idElement=0, numEBOs = 1;
		private int[] VAOs = new int[numVAOs];
		private int[] VBOs = new int[numVBOs];
		private int[] EBOs = new int[numEBOs];

		//Model parameters
		private int[] numElements = new int[numEBOs];

		private long coordSize;
		private long normalSize;
		private long texSize;
		private int ModelView;
		private int NormalTransform;
		private int Projection;
		private int AmbientProduct;
		private int DiffuseProduct;
		private int SpecularProduct;
		private int Shininess;

		private float[] ambient;
		private float[] diffuse;
		private float[] specular;
		private float  materialShininess;



		@Override
		public void display(GLAutoDrawable drawable) {
			GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this

			gl.glClear(GL_COLOR_BUFFER_BIT|GL_DEPTH_BUFFER_BIT);

			T.initialize();
			T.rotateX(15);
			T.rotateY(15);
			T.rotateZ(15);
			T.scale(0.3f, 0.3f, 0.3f);

			// send the lighting parameters to the vertex shader
			gl.glUniform4fv(AmbientProduct,1, ambient,0 );
			gl.glUniform4fv(DiffuseProduct,1, diffuse,0 );
			gl.glUniform4fv(SpecularProduct,1, specular,0 );
			gl.glUniform1f(Shininess, materialShininess);

			gl.glUniformMatrix4fv( ModelView, 1, true, T.getTransformv(), 0 );
			gl.glUniformMatrix4fv( NormalTransform, 1, true, T.getInvTransformTv(), 0 );

			gl.glDrawElements(GL_TRIANGLES, numElements[0], GL_UNSIGNED_INT, 0);
		}

		@Override
		public void dispose(GLAutoDrawable drawable) {
			System.exit(0);
		}

		@Override
		public void init(GLAutoDrawable drawable) {
			GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object this
			System.out.print("GL_Version: " + gl.glGetString(GL_VERSION));
			gl.glEnable(GL_CULL_FACE);

			//read in the image file and assign it to the texture object
			try {
				texture = TextureIO.newTexture(new File("WelshDragon.jpg"), false);
			} catch (IOException ex) {
				ex.printStackTrace();
			}

			//compile and use the shader program
			ShaderProg shaderproc = new ShaderProg(gl, "Texture.vert", "Texture.frag");
			int program = shaderproc.getProgram();
			gl.glUseProgram(program);

			//Create the cube
			SObject cube = new SCube();
			float[] vertexCoord = cube.getVertices();
			float[] normalArray = cube.getNormals();
			float[] texCoord = cube.getTextures();
			int[] vertexIndices = cube.getIndices();
			numElements[idElement] = cube.getNumIndices();

			// define the lighting values
			float[] lightPosition = {10.0f, 15.0f, 20.0f, 1.0f};
			Vec4 lightAmbient = new Vec4(0.1f, 0.1f, 0.1f, 1.0f);
			Vec4 lightDiffuse = new Vec4(1, 1, 1, 1);  // Adjusted diffuse value
			Vec4 lightSpecular = new Vec4(1, 1, 1, 1);

			Vec4 materialAmbient  = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);
			Vec4 materialDiffuse  = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);
			Vec4 materialSpecular = new Vec4(1.0f, 1.0f, 1.0f, 1.0f);
			materialShininess = 4.0f;

			// assign the lighting values to send to the vertex shader in display()
			Vec4 ambientProduct = lightAmbient.times(materialAmbient);
			ambient = ambientProduct.getVector();
			Vec4 diffuseProduct = lightDiffuse.times(materialDiffuse);
			diffuse = diffuseProduct.getVector();
			Vec4 specularProduct = lightSpecular.times(materialSpecular);
			specular = specularProduct.getVector();

			// Get connected with the ModelView, NormalTransform, and Projection matrices
			// in the vertex shader
			ModelView = gl.glGetUniformLocation(program, "ModelView");
			NormalTransform = gl.glGetUniformLocation(program, "NormalTransform");
			Projection = gl.glGetUniformLocation(program, "Projection");

			// Get connected with uniform variables AmbientProduct, DiffuseProduct,
			// SpecularProduct, and Shininess in the vertex shader
			AmbientProduct = gl.glGetUniformLocation(program, "AmbientProduct");
			DiffuseProduct = gl.glGetUniformLocation(program, "DiffuseProduct");
			SpecularProduct = gl.glGetUniformLocation(program, "SpecularProduct");
			Shininess = gl.glGetUniformLocation(program, "Shininess");

			gl.glUniform4fv(gl.glGetUniformLocation(program, "LightPosition"),1, lightPosition,0 );

			// Generate vertex array, vertex buffer, and element buffer
			gl.glGenVertexArrays(numVAOs,VAOs,0);
			gl.glBindVertexArray(VAOs[idPoint]);

			gl.glGenBuffers(numVBOs, VBOs,0);
			gl.glBindBuffer(GL_ARRAY_BUFFER, VBOs[idBuffer]);

			gl.glGenBuffers(numEBOs, EBOs,0);
			gl.glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, EBOs[idElement]);

			FloatBuffer data = FloatBuffer.wrap(vertexCoord);
			FloatBuffer normals = FloatBuffer.wrap(normalArray);
			FloatBuffer textures = FloatBuffer.wrap(texCoord);
			IntBuffer elements = IntBuffer.wrap(vertexIndices);

			// calculate the size of each section for the  buffer
			coordSize = vertexCoord.length*(Float.SIZE/8);
			normalSize = normalArray.length*(Float.SIZE/8);
			texSize = texCoord.length*(Float.SIZE/8);
			long indexSize = vertexIndices.length*(Integer.SIZE/8);
			gl.glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexSize, elements, GL_STATIC_DRAW);

			// using the sizes to offset the data entry
			gl.glBufferData(GL_ARRAY_BUFFER, coordSize + normalSize + texSize, null, GL_STATIC_DRAW);

			// Load the real data separately.  We put the textures right after the vertex coordinates,
			// so, the offset for v is the size of vertices in bytes
			gl.glBufferSubData( GL_ARRAY_BUFFER, 0, coordSize, data);
			gl.glBufferSubData( GL_ARRAY_BUFFER, coordSize, normalSize, normals);
			gl.glBufferSubData( GL_ARRAY_BUFFER, coordSize + normalSize, texSize, textures);

			// Initialize the vertex position attribute in the vertex shader
			int vPosition = gl.glGetAttribLocation( program, "vPosition" );
			gl.glEnableVertexAttribArray(vPosition);
			gl.glVertexAttribPointer(vPosition, 3, GL_FLOAT, false, 0, 0L);

			// Initialise the vertex normal in the shader
			int vNormal = gl.glGetAttribLocation( program, "vNormal" );
			gl.glEnableVertexAttribArray(vNormal);
			gl.glVertexAttribPointer(vNormal,       3, GL_FLOAT, false, 0, coordSize);

			// Initialise the vertex texture coordinate in the shader
			int vTextureCoord = gl.glGetAttribLocation( program, "vTexCoord" );
			gl.glEnableVertexAttribArray(vTextureCoord);
			gl.glVertexAttribPointer(vTextureCoord, 2, GL_FLOAT, false, 0, coordSize + normalSize);

			// This is necessary. Otherwise, the color on back face may display
			gl.glDepthFunc(GL_LESS);
			gl.glEnable(GL_DEPTH_TEST);
		}

		@Override
		public void reshape(GLAutoDrawable drawable, int x, int y, int w,
							int h) {

			GL3 gl = drawable.getGL().getGL3(); // Get the GL pipeline object

			gl.glViewport(x, y, w, h);

			T.initialize();

			//projection
			if(h<1){h=1;}
			if(w<1){w=1;}
			float a = (float) w/ h;   //aspect
			if (w < h) {
				T.ortho(-1, 1, -1/a, 1/a, -1, 1);
			}
			else{
				T.ortho(-1*a, 1*a, -1, 1, -1, 1);
			}

			// Convert right-hand to left-hand coordinate system
			T.reverseZ();
			gl.glUniformMatrix4fv( Projection, 1, true, T.getTransformv(), 0 );
		}
	}
}