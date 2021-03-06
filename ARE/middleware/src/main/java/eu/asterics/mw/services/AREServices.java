/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 *
 *
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b.
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.    
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b. 
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P"
 *
 *
 *                    homepage: http://www.asterics.org
 *
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 * 
 * 
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */

package eu.asterics.mw.services;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Point;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JInternalFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import eu.asterics.mw.are.AREStatus;
import eu.asterics.mw.are.DeploymentManager;
import eu.asterics.mw.are.exceptions.AREAsapiException;
import eu.asterics.mw.are.exceptions.DeploymentException;
import eu.asterics.mw.are.exceptions.ParseException;
import eu.asterics.mw.are.parsers.DefaultDeploymentModelParser;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.model.deployment.impl.ModelState;
import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;


/*
 *    AsTeRICS - Assistive Technology Rapid Integration and Construction Set
 *
 *
 *        d8888      88888888888       8888888b.  8888888 .d8888b.   .d8888b.
 *       d88888          888           888   Y88b   888  d88P  Y88b d88P  Y88b
 *      d88P888          888           888    888   888  888    888 Y88b.    
 *     d88P 888 .d8888b  888   .d88b.  888   d88P   888  888         "Y888b. 
 *    d88P  888 88K      888  d8P  Y8b 8888888P"    888  888            "Y88b.
 *   d88P   888 "Y8888b. 888  88888888 888 T88b     888  888    888       "888
 *  d8888888888      X88 888  Y8b.     888  T88b    888  Y88b  d88P Y88b  d88P
 * d88P     888  88888P' 888   "Y8888  888   T88b 8888888 "Y8888P"   "Y8888P"
 *
 *
 *                    homepage: http://www.asterics.org
 *
 *     This project has been partly funded by the European Commission,
 *                      Grant Agreement Number 247730
 * 
 * 
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 *
 */


/**
 * This class provides ARE functionality to software components outside the ARE 
 * 
 *         Date: Aug 25, 2010
 *         Time: 11:35:35 AM
 */

public class AREServices implements IAREServices{

	private final String MODELS_FOLDER = "models"; 
	private final String STORAGE_FOLDER = "storage";
	private Logger logger = null;

	private ArrayList<IAREEventListener> areEventListenerObjects;
	public static final AREServices instance = 
			new AREServices();


	private AREServices()
	{
		super();
		logger = AstericsErrorHandling.instance.getLogger();
		areEventListenerObjects = new ArrayList <IAREEventListener>();

	}


	/**
	 * Deploys the model associated to the specified filename. The file 
	 * should be already available on the ARE file system.
	 * @param filename the filename of the model to be deployed
	 */
	public void deployFile(String filename) {


		filename = MODELS_FOLDER + "/" + filename;
		final IRuntimeModel currentRuntimeModel
		= DeploymentManager.instance.getCurrentRuntimeModel();

		if(currentRuntimeModel != null)
		{
			this.stopModel();
			DeploymentManager.instance.undeployModel();
		}

		try{


			//this is for getting the text xml and converting it to string
			String xmlFile = filename;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			synchronized (builder) {

				Document doc = builder.parse(new File(xmlFile));
				DOMSource domSource = new DOMSource(doc);
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
				transformer.transform(domSource, result);
				String modelInString = writer.toString();
				//calling the asapi function with a string representation of the model
				deployModel(modelInString);
				logger.fine(this.getClass().getName()+"." +
						"deployFile: OK\n");
			}

		}catch (SAXException e3) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e3.getMessage());

		} catch (IOException e4) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e4.getMessage());

		} catch (ParserConfigurationException e5) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e5.getMessage());

		} catch (TransformerConfigurationException e6) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e6.getMessage());

		} catch (TransformerException e7) {
			logger.warning(this.getClass().getName()+"." +
					"deployFile: Failed to deploy file -> \n"
					+e7.getMessage());
		}
	}

	/**
	 * Deploys the model associated to the specified filename. The file 
	 * should be already available on the ARE file system. 
	 * This method will also start the model as soon as it is deployed.
	 * @param filename the filename of the model to be deployed
	 */
	public void deployAndStartFile(String filename) {


		filename = MODELS_FOLDER + "/" + filename;
		final IRuntimeModel currentRuntimeModel
		= DeploymentManager.instance.getCurrentRuntimeModel();


		try{


			//this is for getting the text xml and converting it to string
			String xmlFile = filename;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			synchronized (builder) {
				Document doc = builder.parse(new File(xmlFile));
				DOMSource domSource = new DOMSource(doc);
				StringWriter writer = new StringWriter();
				StreamResult result = new StreamResult(writer);
				TransformerFactory tf = TransformerFactory.newInstance();
				Transformer transformer = tf.newTransformer();
				transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-16");
				transformer.transform(domSource, result);

				String modelInString = writer.toString();

				if(currentRuntimeModel != null)
				{
					this.stopModel();
					DeploymentManager.instance.undeployModel();
				}
				//calling the asapi function with a string 
				//representation of the model
				deployModel(modelInString);
				logger.fine(this.getClass().getName()+"." +
						"deployAndStartFile: OK\n");
				runModel();

			}
		}catch (SAXException e3) {
			logger.warning(this.getClass().getName()+"." +
					"deployAndStartFile: Failed to deploy file -> \n"
					+e3.getMessage());

		} catch (IOException e4) {
			logger.warning(this.getClass().getName()+"." +
					"deployAndStartFile: Failed to deploy file -> \n"
					+e4.getMessage());

		} catch (ParserConfigurationException e5) {
			logger.warning(this.getClass().getName()+"." +
					"deployAndStartFile: Failed to deploy file -> \n"
					+e5.getMessage());

		} catch (TransformerConfigurationException e6) {
			logger.warning(this.getClass().getName()+"." +
					"deployAndStartFile: Failed to deploy file -> \n"
					+e6.getMessage());

		} catch (TransformerException e7) {
			logger.warning(this.getClass().getName()+"." +
					"deployAndStartFile: Failed to deploy file -> \n"
					+e7.getMessage());
		} catch (AREAsapiException e) {
			logger.warning(this.getClass().getName()+"." +
					"deployAndStartFile: Failed to start file -> \n"
					+e.getMessage());
		}
	}

	/**
	 * Sets the property with the specified key in the component with the
	 * specified ID with the given string representation of the value.
	 *
	 * @param componentID the ID of the component to be checked
	 * @param key the key of the property to be set
	 * @param value the string-representation of the value to be set to the
	 * specified key
	 * @return the previous value of the property with the specified key in the
	 * component with the specified ID as a string, or an empty string if the
	 * property was not previously set
	 */
	public String setComponentProperty(String componentID, String key,
			String value) {
		String result = DeploymentManager.instance.getCurrentRuntimeModel().
				setComponentProperty(componentID, key, value);
		DeploymentManager.instance.setComponentProperty (componentID, key, value);
		if (result == null)
		{
			logger.warning(this.getClass().getName()+"."+
					"setComponentProperty: Undefined component "+
					componentID+"\n");
			return "";
		}
		else
		{
			logger.fine(this.getClass().getName()+"."+
					"setComponentProperty: OK\n");
			return result;
		}
	}


	/**
	 * Stops the execution of the model. Unlike the {@link #pauseModel()}
	 * method, this one resets the components, which means that when the model
	 * is started again it starts from scratch (i.e., with a new state).
	 */
	public void stopModel()
	{
		if (DeploymentManager.instance.getStatus()==AREStatus.RUNNING
				||DeploymentManager.instance.getStatus()==AREStatus.PAUSED)
		{
			DeploymentManager.instance.stopModel();
			DeploymentManager.instance.getCurrentRuntimeModel().
			setState(ModelState.STOPPED);
			DeploymentManager.instance.setStatus(AREStatus.OK);
			AstericsErrorHandling.instance.setStatusObject(AREStatus.OK.toString(), 
					"", "");
			logger.fine(this.getClass().getName()+".stopModel: model stopped \n");
		}
	}

	/**
	 * Deploys the model encoded in the specified string into the ARE. An
	 * exception is thrown if the specified string is either not well-defined
	 * XML, or not well defined ASAPI model encoding, or if a validation error
	 * occurred after reading the model.
	 * @param modelInXML a string representation in XML of the model to be
	 * deployed
	 */
	private void deployModel(final String modelInXML)
	{
		//Stop running model first if there is one
		if (DeploymentManager.instance.getStatus()==AREStatus.RUNNING)
		{
			stopModel();	
			DeploymentManager.instance.undeployModel();
		}
		DefaultDeploymentModelParser defaultDeploymentModelParser = 
				DefaultDeploymentModelParser.instance;

		File modelFile = new File(MODELS_FOLDER+"/model.xml");
		File modelsDir = new File(MODELS_FOLDER);
		if (!modelFile.exists())
		{
			try {
				modelsDir.mkdir();
				modelFile.createNewFile();
			} catch (IOException e1) {
				DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
				AstericsErrorHandling.instance.setStatusObject
				(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
				logger.warning(this.getClass().getName()+"." +
						"deployModel: Failed to create file model.xml -> \n"
						+e1.getMessage());

			}
		}

		//Convert the string to a byte array.
		String s = modelInXML;
		byte data[] = s.getBytes();
		try {

			BufferedWriter c = new BufferedWriter(new OutputStreamWriter
					(new FileOutputStream(modelFile),"UTF-16"));

			//out = new BufferedOutputStream(new FileOutputStream(modelFile));
			for (int i=0; i<data.length; i++)
				c.write(data[i]);

			if (c != null) {
				c.flush();
				c.close();
			}
			InputStream is = new ByteArrayInputStream(modelInXML.getBytes("UTF-16"));
			IRuntimeModel runtimeModel = 
					defaultDeploymentModelParser.parseModel(is);

			/*if (runtimeModel==null)
			{
				logger.fine("Failed to create model");
			}*/

			DeploymentManager.instance.deployModel(runtimeModel);
			DeploymentManager.instance.setStatus(AREStatus.DEPLOYED);
			AstericsErrorHandling.instance.setStatusObject(AREStatus.DEPLOYED.toString(), 
					"", "");
		}  catch (IOException e2) {
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject
			(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName()+"." +
					"deployModel: Failed to deploy model -> \n"
					+e2.getMessage());

		} catch (DeploymentException e3) {
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject
			(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName()+"." +
					"deployModel: Failed to deploy model -> \n"
					+e3.getMessage());

		} catch (ParseException e4) {
			DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
			AstericsErrorHandling.instance.setStatusObject
			(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
			logger.warning(this.getClass().getName()+"." +
					"deployModel: Failed to deploy model -> \n"
					+e4.getMessage());

		}
	}

	/**
	 * It starts or resumes the execution of the model.
	 */
	public void runModel()	throws AREAsapiException
	{
		if (DeploymentManager.instance.getCurrentRuntimeModel().getState().
				equals(ModelState.STOPPED))
		{
			DeploymentManager.instance.runModel();	
		}
		else 
		{
			DeploymentManager.instance.resumeModel();
		}
		DeploymentManager.instance.getCurrentRuntimeModel().
		setState(ModelState.STARTED);
		DeploymentManager.instance.setStatus(AREStatus.RUNNING);
		AstericsErrorHandling.instance.setStatusObject(AREStatus.RUNNING.toString(), 
				"", "");
		logger.fine(this.getClass().getName()+".runModel: model running \n");
	}

	/**
	 * 
	 * @return
	 */
	public boolean isAREStoppedAndHealthy()
	{
		AREStatus status = DeploymentManager.instance.getStatus();
		if ((status == AREStatus.UNKNOWN) || (status == AREStatus.OK) )
		{
			return true;
		}
		return false;
	}


	/**
	 * Provides the name of the currently deployed model in ARE.
	 * 
	 * @return the name of the model as a String object if there is one 
	 * deployed, <code>null</code> otherwise.
	 */
	public String getRuntimeModelName()
	{
		IRuntimeModel currentRuntimeModel = DeploymentManager.instance.
				getCurrentRuntimeModel();
		if(currentRuntimeModel != null)
		{
			return currentRuntimeModel.getModelName();
		}
		return null;
	}

	/**
	 * Opens a File object for the requested file. The method will look up the 
	 * current model name and the instance name of the component and open or
	 * create the file if it does not exist. The file will exist in a folder
	 * tree which allows each instance of a component to have its own storage
	 * on a per instance per model basis
	 * 
	 * @return the File object if it has been created, <code>null</code> if
	 * there is currently no model deployed or the object could not be created.
	 * 
	 * @param component	the requesting instance of a runtime component
	 * 
	 * @param fileName	the name of the file to be opened
	 */
	public File getLocalStorageFile(IRuntimeComponentInstance component, 
			String fileName)
	{
		String modelName = getRuntimeModelName();

		if (modelName == null)
		{
			// no model running, storage not available
			return null;
		}

		StringBuffer fullFilePath = new StringBuffer(STORAGE_FOLDER);
		fullFilePath.append("/");
		fullFilePath.append(modelName);
		fullFilePath.append("/");
		fullFilePath.append(DeploymentManager.instance
				.getComponentInstanceIDFromComponentInstance(component));
		fullFilePath.append("/");

		File localDir  = new File(fullFilePath.toString());
		File localFile = new File(fullFilePath.toString() + fileName);

		if (!localFile.exists())
		{
			try {
				localDir.mkdirs();
				localFile.createNewFile();
			} catch (IOException e1) {
				DeploymentManager.instance.setStatus(AREStatus.FATAL_ERROR);
				AstericsErrorHandling.instance.setStatusObject
				(AREStatus.FATAL_ERROR.toString(), "", "Deployment Error");
				logger.warning(this.getClass().getName()+"." +
						"deployModel: Failed to create file " + fullFilePath.
						toString() + fileName + "-> \n"+e1.getMessage());
			}
		}
		return localFile;
	}


	public synchronized void registerAREEventListener(IAREEventListener clazz) {

		if (!this.areEventListenerObjects.contains(clazz) && clazz!=null)
		{
			this.areEventListenerObjects.add(clazz);
		}	

	}


	public synchronized void unregisterAREEventListener(IAREEventListener clazz) {

		Iterator<IAREEventListener> itr = this.areEventListenerObjects.iterator();
		IAREEventListener listener;
		while (itr.hasNext())
		{
			listener=(IAREEventListener) itr.next();
			if (listener.equals(clazz))
			{
				itr.remove();
				return;
			}
		}
	}

	public synchronized ArrayList<IAREEventListener> getAREEventListners ()
	{
		if (this.areEventListenerObjects!=null)
			return this.areEventListenerObjects;
		else 
			return new ArrayList<IAREEventListener>();	
	}

	/**
	 * @deprecated
	 */
	public void displayFrame(JInternalFrame internalFrame, boolean display) {
		DeploymentManager.instance.displayFrame (internalFrame, display);

	}

	public void displayPanel(JPanel panel, 
			IRuntimeComponentInstance componentInstance, boolean display) {
		DeploymentManager.instance.displayPanel (panel, componentInstance, 
				display);

	}


	public Dimension getAvailableSpace(IRuntimeComponentInstance componentInstance)
	{

		return DeploymentManager.instance.getAvailableSpace(componentInstance);

	}

	public Point getComponentPosition (IRuntimeComponentInstance componentInstance)
	{

		return DeploymentManager.instance.getComponentPosition(componentInstance);

	}




	public void adjustFonts(JPanel panel, int maxFontSize, int minFontSize, 
			int offset) 
	{

		
		
			adjustPanelFonts (panel, maxFontSize, minFontSize, offset);
			Component[] components = panel.getComponents();
//		if (components.length>0)
//		{
//			for (Component c : components)
//			{
//				System.out.println ("Component: "+c.getName());
//				if (c instanceof JPanel)
//				{
//					System.out.println ("Instance of JPanel");
//
//					adjustPanelFonts ((JPanel) c, maxFontSize, minFontSize, offset);
//				}
//			}
//		}

	}

	private void adjustPanelFonts(JPanel c, int maxFontSize, int minFontSize, 
			int offset) 
	{



		int containerWidth = c.getPreferredSize().width-offset;
		int containerHeight = c.getPreferredSize().height;


		Component[] comp = c.getComponents();

		
		
		for (int i=0;i<comp.length;++i) 
		{
			if (JLabel.class.isAssignableFrom(comp[i].getClass()) )
			{
				JLabel label = (JLabel) comp[i];
				Font labelFont = label.getFont();
				String labelText = label.getText();
				if (labelText.length() > 0)
				{
					int stringWidth = 
							label.getFontMetrics(labelFont).stringWidth(labelText);
					double ratio = Math.min(containerWidth,containerHeight) 
							/ (double)stringWidth;
					int newFontSize = (int)(labelFont.getSize() * ratio);

					int fs = Math.min(newFontSize, containerHeight);
					fs = Math.max(newFontSize, minFontSize);
					fs = Math.min(fs, maxFontSize);

					label.setFont(new Font(labelFont.getName(), 
							labelFont.getStyle(), fs));

				}


			}
			else if (JTextComponent.class.isAssignableFrom(comp[i].getClass()))
			{

				JTextComponent label = (JTextComponent) comp[i];
				Font labelFont = label.getFont();
				String labelText = label.getText();
				if (labelText.length() > 0)
				{
					int stringWidth = 
							label.getFontMetrics(labelFont).stringWidth(labelText);
					double ratio = Math.min(containerWidth,containerHeight) 
							/ (double)stringWidth;
					int newFontSize = (int)(labelFont.getSize() * ratio);


					int fs = Math.min(newFontSize, containerHeight);
					fs = Math.max(newFontSize, minFontSize);
					fs = Math.min(fs, maxFontSize);
					label.setFont(new Font(labelFont.getName(), 
							labelFont.getStyle(), fs));

				}


			}
			else if (JButton.class.isAssignableFrom(comp[i].getClass()))
			{

				JButton label = (JButton) comp[i];
				Font labelFont = label.getFont();
				String labelText = label.getText();
				if (labelText.length() > 0)
				{
					int stringWidth = 
							label.getFontMetrics(labelFont).stringWidth(labelText);
					double ratio = Math.min(containerWidth,containerHeight) 
							/ (double)stringWidth;
					int newFontSize = (int)(labelFont.getSize() * ratio);


					int fs = Math.min(newFontSize, containerHeight);
					fs = Math.max(newFontSize, minFontSize);
					fs = Math.min(fs, maxFontSize);

					label.setFont(new Font(labelFont.getName(), 
							labelFont.getStyle(), fs));
				}
			}
			else if (JSlider.class.isAssignableFrom(comp[i].getClass()))
			{

				JSlider slider = (JSlider) comp[i];
				Font sliderFont = slider.getFont();
				int newFontSize=minFontSize;
				int fs=minFontSize;
				
//					double ratio = Math.min(containerWidth,containerHeight);
//					int newFontSize = (int)(sliderFont.getSize() * ratio);
//
//
//					int fs = Math.min(newFontSize, containerHeight);
				
				if (containerWidth>0&&containerWidth<=25)
					newFontSize=6;
				else if (containerWidth>25&&containerWidth<=50)
					newFontSize=8;
				else if (containerWidth>50&&containerWidth<=100)
					newFontSize=10;
				else if (containerWidth>100&&containerWidth<=400)
					newFontSize=14;
				else if (containerWidth>400)
					newFontSize=16;
				
					fs = Math.max(newFontSize, minFontSize);
					fs = Math.min(fs, maxFontSize);

					slider.setFont(new Font(sliderFont.getName(), 
							sliderFont.getStyle(), fs));
				
			}
			
			if (comp[i] instanceof JPanel) 
			{
				adjustPanelFonts( (JPanel)comp[i], maxFontSize, minFontSize, offset);
			}

		}
		
		//Adjust TitledBorders if any
//		Border b = c.getBorder();
//		if (TitledBorder.class.isAssignableFrom(b.getClass()))
//		{
//
//			TitledBorder tb = (TitledBorder) b;
//		
//			Font tbFont = tb.getTitleFont();
//			
//			
//			double ratio = Math.min(containerWidth,containerHeight) 
//					/ (double)containerWidth;
//			int newFontSize = (int)(tbFont.getSize() * ratio);
//
//
//			int fs = Math.min(newFontSize, containerHeight);
//			fs = Math.max(newFontSize, minFontSize);
//			fs = Math.min(fs, maxFontSize);
//
//			tb.setTitleFont(new Font(tbFont.getName(), 
//					tbFont.getStyle(), fs));
//			
//		}
	}

}
