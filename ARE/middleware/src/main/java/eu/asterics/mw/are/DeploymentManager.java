package eu.asterics.mw.are;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import eu.asterics.mw.are.AREEvent;
import eu.asterics.mw.are.exceptions.BundleManagementException;
import eu.asterics.mw.are.exceptions.DeploymentException;
import eu.asterics.mw.data.ByteToDoubleWrapper;
import eu.asterics.mw.data.ByteToIntegerWrapper;
import eu.asterics.mw.data.CharToDouble;
import eu.asterics.mw.data.CharToInteger;
import eu.asterics.mw.data.DoubleToIntegerWrapper;
import eu.asterics.mw.data.IntegerToDoubleWrapper;
import eu.asterics.mw.gui.AstericsGUI;
import eu.asterics.mw.model.DataType;
import eu.asterics.mw.model.bundle.IComponentType;
import eu.asterics.mw.model.deployment.IBindingEdge;
import eu.asterics.mw.model.deployment.IChannel;
import eu.asterics.mw.model.deployment.IComponentInstance;
import eu.asterics.mw.model.deployment.IEventChannel;
import eu.asterics.mw.model.deployment.IEventEdge;
import eu.asterics.mw.model.deployment.IInputPort;
import eu.asterics.mw.model.deployment.IRuntimeModel;
import eu.asterics.mw.model.deployment.impl.AREGUIElement;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.services.AREServices;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.IAREEventListener;


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
 * The deployment manager is responsible for managing the deployment of models 
 * (deploy/undeploy a model, start/stop a model etc.). It also keeps track of 
 * component instances at runtime.
 *
 * @author Nearchos Paspallis [nearchos@cs.ucy.ac.cy]
 *         Date: Aug 20, 2010
 *         Time: 12:59:28 PM
 */
public class DeploymentManager
{
	private Logger logger = null;
	private ComponentRepository componentRepository = ComponentRepository.instance;
	public static final DeploymentManager instance = new DeploymentManager();
	private AstericsGUI gui=null;

	//TODO REPLACE WIDTH A FUNCTION THAT RETRIEVES X, Y, W, H FROM MODEL
	static int X=10;
	static int Y=15;
	static int W = 580;
	static int H = 700;
	static int OFFSET = 5;

	private DeploymentManager()
	{
		super();
		logger = AstericsErrorHandling.instance.getLogger();
	}

	private AREStatus areStatus;

	public void setStatus (AREStatus status)
	{
		areStatus = status; 
		gui.setStatus(status);
	}

	public AREStatus getStatus()
	{
		return areStatus;
	}




	private IRuntimeModel currentRuntimeModel = null;

	void start(final BundleContext bundleContext)
	{
		// this.bundleContext = bundleContext;
		// todo load default deployment descriptor from file into
		// currentRuntimeModel
	}

	void stop()
	{
		// todo save currentRuntimeModel deployment descriptor to file
	}

	private Map<String, IRuntimeComponentInstance> runtimeComponentInstances
	= new HashMap<String, IRuntimeComponentInstance>();
	private Map<String, Set<IRuntimeComponentInstance>> 
	componentTypeIdToRuntimeComponentInstances
	= new HashMap<String, Set<IRuntimeComponentInstance>>();
	private BundleManager bundleManager;
	private Map<IRuntimeComponentInstance,String > runtimeInstanceToComponentTypeID = new HashMap<IRuntimeComponentInstance, String>();
	private Map<String, Stack<HashMap<String, byte[]>>> bufferedPortsMap
	= new HashMap<String, Stack<HashMap<String,byte[]>>>();

	private void init()
	{
		// unget components
		runtimeComponentInstances.clear();
		componentTypeIdToRuntimeComponentInstances.clear();

		// todo unget components
	}


	/**
	 * After the deployment model has been retrieved from the ACS and 
	 * successfully parsed a IRuntimeModel object is created and passed to this 
	 * method for deploying the model.
	 * @param runtimeModel the object generated after parsing a deployment model
	 * @throws DeploymentException
	 */
	public void deployModel(final IRuntimeModel runtimeModel)
			throws DeploymentException
			{
		if (runtimeModel == null)
		{
			logger.severe(this.getClass().getName()+
					": install-> Illegal null argument");
			throw new RuntimeException("Illegal null argument");
		}

		notifyAREEventListeners (AREEvent.PRE_DEPLOY_EVENT);


		final Set<IComponentInstance> componentInstanceSet =
				runtimeModel.getComponentInstances();

		// handle instantiation of new component instances and assign property
		// values as needed

		String conversion="";
		for (IComponentInstance componentInstance : componentInstanceSet)
		{
			final String componentTypeID =
					componentInstance.getComponentTypeID();


			final IComponentType componentType = 
					componentRepository.getComponentType(componentTypeID);

			if (componentType == null)
			{
				logger.severe("Could not find component " +
						"type: " + componentTypeID);
				throw new DeploymentException("Could not find component " +
						"type: " + componentTypeID);
			}

			final String canonicalName = componentType.getCanonicalName();

			IRuntimeComponentInstance runtimeComponentInstance;

			try{
				runtimeComponentInstance =
						componentRepository.getInstance(canonicalName);

			}
			catch (NullPointerException e){

				JOptionPane.showMessageDialog(null,
						canonicalName+ " prevents the model from starting!",
						"ARE runtime error",
						JOptionPane.WARNING_MESSAGE);
				return;
			}

			//Set runtime property values to component instances
			final Set<String> propertyNames = componentType.getPropertyNames();
			if (propertyNames != null)
			{
				for (final String propertyName : propertyNames)
				{
					final Object propertyValue = 
							componentInstance.getPropertyValue(propertyName);
					if (propertyName != null)
					{
						runtimeComponentInstance.
						setRuntimePropertyValue(propertyName, propertyValue);
					}
				}
			}

			runtimeComponentInstances.put(componentInstance.getInstanceID(),
					runtimeComponentInstance);
			runtimeInstanceToComponentTypeID.put(runtimeComponentInstance, 
					componentInstance.getComponentTypeID());

			Set<IRuntimeComponentInstance> set =
					componentTypeIdToRuntimeComponentInstances.
					get(componentInstance.getComponentTypeID());
			if (set == null)
			{
				set = new HashSet<IRuntimeComponentInstance>();
			}
			set.add(runtimeComponentInstance);

			componentTypeIdToRuntimeComponentInstances.
			put(componentInstance.getComponentTypeID(), set);
		}

		// handle channel formation
		final Set<IChannel> channels = runtimeModel.getChannels();

		for (final IChannel channel : channels)
		{
			final IBindingEdge sourceBindingEdge = channel.getSource();
			final IBindingEdge targetBindingEdge = channel.getTarget();

			final String sourceComponentInstanceID = sourceBindingEdge.
					getComponentInstanceID();
			final String sourceOutputPortID = sourceBindingEdge.getPortID();

			final String targetComponentInstanceID = targetBindingEdge.
					getComponentInstanceID();
			final String targetInputPortID = targetBindingEdge.getPortID();

			

			final IRuntimeComponentInstance sourceComponentInstance
			= runtimeComponentInstances.get(sourceComponentInstanceID);

			final IRuntimeOutputPort sourceRuntimeOutputPort
			= sourceComponentInstance.getOutputPort(sourceOutputPortID);

			final IRuntimeComponentInstance targetComponentInstance
			= runtimeComponentInstances.get(targetComponentInstanceID);

			final IRuntimeInputPort targetRuntimeInputPort
			= targetComponentInstance.getInputPort(targetInputPortID);

			// form the binding
			//The targetRuntimeInputPort is the same for averager and targetComponentInstance are the same

			// 1. find out the data types of the source and target ports
			// 2. if not the same and compatible, then
			// -- select appropriate wrapper and use that instead
			final IRuntimeInputPort wrapper;

			final DataType sourceDataType = runtimeModel.
					getPortDataType(sourceComponentInstanceID, sourceOutputPortID);
			final DataType targetDataType = runtimeModel.
					getPortDataType(targetComponentInstanceID, targetInputPortID);
			
			conversion = "";
			if(sourceDataType != targetDataType)
			{
				switch (sourceDataType)
				{
				case BYTE:
					switch (targetDataType)
					{
					case INTEGER:
						wrapper = new ByteToIntegerWrapper(targetRuntimeInputPort);
						conversion="byteToInteger";
						break;
					case DOUBLE:
						wrapper = new ByteToDoubleWrapper(targetRuntimeInputPort);
						conversion="byteToDouble";
						break;
					default:
						logger.warning("Incompatible conversion " +
								"from: " + sourceDataType + " to: " + 
								targetDataType);
						throw new RuntimeException("Incompatible conversion " +
								"from: " + sourceDataType + " to: " + 
								targetDataType);
					}
					break;
				case CHAR:
					switch (targetDataType)
					{
					case INTEGER:
						wrapper = new CharToInteger(targetRuntimeInputPort);
						conversion="charToInteger";
						break;
					case DOUBLE:
						wrapper = new CharToDouble(targetRuntimeInputPort);
						conversion="charToDouble";
						break;
					default:
						logger.warning("Incompatible conversion " +
								"from: " + sourceDataType + " to: " + 
								targetDataType);
						throw new RuntimeException("Incompatible conversion " +
								"from: " + sourceDataType + " to: " +
								targetDataType);
					}
					break;
				case INTEGER:
					switch (targetDataType)
					{
					case DOUBLE:
						
						wrapper = new IntegerToDoubleWrapper(targetRuntimeInputPort);
						conversion="integerToDouble";
						break;
					default:
						logger.warning("Incompatible conversion " +
								"from: " + sourceDataType + " to: " + 
								targetDataType);
						throw new RuntimeException("Incompatible conversion " +
								"from: " + sourceDataType + " to: " + 
								targetDataType);
					}
					break;
				case DOUBLE:
					switch (targetDataType)
					{
					case INTEGER:
						wrapper = new DoubleToIntegerWrapper(targetRuntimeInputPort);
						conversion="doubleToInteger";
						break;
					default:
						logger.warning("Incompatible conversion " +
								"from: " + sourceDataType + " to: " + 
								targetDataType);
						throw new RuntimeException("Incompatible conversion " +
								"from: " + sourceDataType + " to: " + 
								targetDataType);
					}
					break;
				case UNKNOWN:
					conversion="";
					logger.severe("Invalid enum type for source data type: "
							+ sourceDataType);
					throw new RuntimeException("Invalid enum type for source " +
							"data type: " + sourceDataType);
				default:
					logger.severe("Invalid enum type for source data type: "
							+ sourceDataType);
					throw new RuntimeException("Invalid enum type for source" +
							" data type: " + sourceDataType);
				}
			}
			else
			{
				wrapper = targetRuntimeInputPort;
			}

			sourceRuntimeOutputPort.addInputPortEndpoint(
					targetComponentInstanceID, 
					targetInputPortID, 
					wrapper,
					conversion);
			
			runtimeModel.getComponentInstance(targetComponentInstanceID).
				setWrapper (targetInputPortID, wrapper);

			//            sourceRuntimeOutputPort.addInputPortEndpoint(targetComponentInstanceID, targetInputPortID, targetRuntimeInputPort);
		}

		// handle event channels
		final Set<IEventChannel> eventChannels = runtimeModel.getEventChannels();
		for (final IEventChannel eventChannel : eventChannels)
		{
			final String eventChannelID = eventChannel.getChannelID();
			final IEventEdge [] eventSources = eventChannel.getSources();
			final IEventEdge [] eventTargets = eventChannel.getTargets();

			final Set<EventListenerDetails> targetEventListenerPorts
			= new HashSet<EventListenerDetails>();

			for(final IEventEdge targetEventEdge : eventTargets)
			{
				final String targetComponentInstanceID = 
						targetEventEdge.getComponentInstanceID();
				final String targetEventPortID = 
						targetEventEdge.getEventPortID();

				final IRuntimeComponentInstance targetComponentInstance
				= runtimeComponentInstances.
				get(targetComponentInstanceID);

				final IRuntimeEventListenerPort eventListenerPort
				= targetComponentInstance.
				getEventListenerPort(targetEventPortID);
				targetEventListenerPorts.add(
						new EventListenerDetails(targetComponentInstanceID, 
								targetEventPortID, 
								eventListenerPort));
			}

			for(final IEventEdge sourceEventEdge : eventSources)
			{
				final String sourceComponentInstanceID = 
						sourceEventEdge.getComponentInstanceID();
				final String sourceEventPortID = sourceEventEdge.
						getEventPortID();
				final IRuntimeComponentInstance sourceComponentInstance
				= runtimeComponentInstances.
				get(sourceComponentInstanceID);
				final IRuntimeEventTriggererPort eventTriggererPort
				= sourceComponentInstance.
				getEventTriggererPort(sourceEventPortID);

				eventTriggererPort.setEventChannelID(eventChannelID);

				for(final EventListenerDetails eventListenerDetails : 
					targetEventListenerPorts)
				{
					eventTriggererPort.addEventListener(
							eventListenerDetails.componentID, 
							eventListenerDetails.portID,
							eventListenerDetails.runtimeEventListenerPort);
				}
			}
		}

		this.currentRuntimeModel = runtimeModel;

		notifyAREEventListeners (AREEvent.POST_DEPLOY_EVENT);
	}


	/**
	 * This method undeploys the model and clears all component instances at runtime.
	 * 
	 */
	public void undeployModel()
	{
		final IRuntimeModel runtimeModel = this.getCurrentRuntimeModel();

		final Set<IChannel> channels = runtimeModel.getChannels();
		final Set <IEventChannel> eventChannels = 
				runtimeModel.getEventChannels();

		//Disconnect channels
		for (IChannel channel : channels)
		{
			final IBindingEdge sourceBindingEdge = channel.getSource();
			final IBindingEdge targetBindingEdge = channel.getTarget();
			final String sourceComponentInstanceID = sourceBindingEdge.
					getComponentInstanceID();
			final String sourceOutputPortID = sourceBindingEdge.getPortID();
			final String targetComponentInstanceID = targetBindingEdge.
					getComponentInstanceID();
			final String targetInputPortID = targetBindingEdge.getPortID();
			final IRuntimeComponentInstance sourceComponentInstance
			= runtimeComponentInstances.get(sourceComponentInstanceID);

			if (sourceComponentInstance!=null)
			{
				final IRuntimeOutputPort sourceRuntimeOutputPort
				= sourceComponentInstance.getOutputPort(sourceOutputPortID);			

				sourceRuntimeOutputPort.
				removeInputPortEndpoint(targetComponentInstanceID, 
						targetInputPortID);
			}

		}
		for (IEventChannel eventChannel : eventChannels)
		{
			final String eventChannelID = eventChannel.getChannelID();
			final IEventEdge [] eventSources = eventChannel.getSources();
			final IEventEdge [] eventTargets = eventChannel.getTargets();

			final Set<EventListenerDetails> targetEventListenerPorts
			= new HashSet<EventListenerDetails>();

			for(final IEventEdge targetEventEdge : eventTargets)
			{
				final String targetComponentInstanceID = 
						targetEventEdge.getComponentInstanceID();
				final String targetEventPortID = 
						targetEventEdge.getEventPortID();

				final IRuntimeComponentInstance targetComponentInstance
				= runtimeComponentInstances.
				get(targetComponentInstanceID);

				if (targetComponentInstance!=null)
				{
					final IRuntimeEventListenerPort eventListenerPort
					= targetComponentInstance.
					getEventListenerPort(targetEventPortID);
					targetEventListenerPorts.add(
							new EventListenerDetails(targetComponentInstanceID, 
									targetEventPortID, 
									eventListenerPort));
				}
			}
			//disconnect event channels
			for(final IEventEdge sourceEventEdge : eventSources)
			{
				final String sourceComponentInstanceID = 
						sourceEventEdge.getComponentInstanceID();
				final String sourceEventPortID = sourceEventEdge.
						getEventPortID();

				final IRuntimeComponentInstance sourceComponentInstance
				= runtimeComponentInstances.
				get(sourceComponentInstanceID);
				if (sourceComponentInstance!=null)
				{
					final IRuntimeEventTriggererPort eventTriggererPort
					= sourceComponentInstance.
					getEventTriggererPort(sourceEventPortID);
					eventTriggererPort.setEventChannelID(eventChannelID);


					for(final EventListenerDetails eventListenerDetails : 
						targetEventListenerPorts)
					{
						eventTriggererPort.
						removeEventListener(eventListenerDetails.componentID,
								eventListenerDetails.portID);
					}
				}
			}
		}

		runtimeComponentInstances.values().clear();
		System.gc();

	}


	/**
	 * This method removes the specified component from the runtime environment.
	 * @param componentID the component to be removed
	 * @throws BundleException, BundleManagementException
	 */
	public void removeComponent(String componentID) throws BundleException,
	BundleManagementException
	{
		IRuntimeComponentInstance ci = runtimeComponentInstances.get(componentID);
		if (ci == null)
			return;
		ci.stop();

		String cType = getCurrentRuntimeModel().
				getComponentInstance(componentID).
				getComponentTypeID();

		runtimeComponentInstances.remove(ci);
		if (cType == null)
			return;
		Set<IRuntimeComponentInstance> set =
				componentTypeIdToRuntimeComponentInstances.
				get(cType);

		if (set != null)
		{
			set.remove(ci);

			if (set.size() == 0)
			{
				componentTypeIdToRuntimeComponentInstances.remove(set);

				//No instances of this type stop the Bundle possible
				BundleManager.
				stopBundleComponent(componentRepository.getComponentType(cType));
			}
		}
	}


	// ---------------------- Model lifecycle support ----------------------- //

	/**
	 * This method runs the deployed model.
	 * 
	 */

	public void runModel()
	{

		notifyAREEventListeners (AREEvent.PRE_START_EVENT);
		for (final IRuntimeComponentInstance componentInstance : runtimeComponentInstances.values())
		{
			try 
			{
				String id = runtimeInstanceToComponentTypeID.get(componentInstance);
				bundleManager.getBundleFromId(id).start();
				componentInstance.start();


				IRuntimeModel runtimeModel = getCurrentRuntimeModel();
				String s = getComponentInstanceIDFromComponentInstance(componentInstance);
				IComponentInstance ci = runtimeModel.getComponentInstance(s);

				Set<IInputPort> bufferedPorts = ci.getBufferedInputPorts();
				Iterator<IInputPort> itr = bufferedPorts.iterator();
				while(itr.hasNext())
				{			
					IInputPort port = itr.next();
					IRuntimeInputPort runtimePort = componentInstance.getInputPort(port.getPortType());
					runtimePort.startBuffering((AbstractRuntimeComponentInstance) componentInstance, port.getPortType());
					
					IRuntimeInputPort wrapper = ci.getWrapper(port.getPortType());
					if (wrapper!=null)
						wrapper.startBuffering((AbstractRuntimeComponentInstance) componentInstance, port.getPortType());
				}
				
			}
			catch (Throwable t) 
			{
				//custom title, error icon
				JOptionPane.showMessageDialog(null,
						componentInstance+ ":\n"+t.getMessage()
						, "ARE Runtime Error",
						JOptionPane.ERROR_MESSAGE);
				t.printStackTrace();
				//System.exit(0);
				stopModel();
			}
		}

	}

	/**
	 * This method pauses the model that is currently at runtime.
	 * 
	 */
	public void pauseModel()
	{
		for (final IRuntimeComponentInstance componentInstance : runtimeComponentInstances.values())
		{
			componentInstance.pause();
		}
	}

	/**
	 * This method resumes the paused model.
	 * 
	 */
	public void resumeModel()
	{
		for (final IRuntimeComponentInstance componentInstance : runtimeComponentInstances.values())
		{
			componentInstance.resume();
		}
	}

	/**
	 * This method stops the model that is currently at runtime
	 * 
	 */
	public void stopModel()
	{
		for (final IRuntimeComponentInstance componentInstance : runtimeComponentInstances.values())
		{
			try 
			{
				String id = runtimeInstanceToComponentTypeID.get(componentInstance);
				bundleManager.getBundleFromId(id).stop();
				componentInstance.stop();
			}
			catch (Throwable t) 
			{
				//custom title, error icon
				JOptionPane.showMessageDialog(null,
						componentInstance+ " Runtime Error!",
						"ARE runtime error",
						JOptionPane.ERROR_MESSAGE);
				//System.exit(0);
			}


		}
		notifyAREEventListeners (AREEvent.POST_STOP_EVENT);
		System.gc();
	}

	// ------------------- End of model lifecycle support ------------------- //

	/**
	 * This method returns the model that is currently at runtime
	 * 
	 */
	public IRuntimeModel getCurrentRuntimeModel()
	{
		return currentRuntimeModel;
	}

	private class EventListenerDetails
	{
		private final String componentID;
		private final String portID;
		private final IRuntimeEventListenerPort runtimeEventListenerPort;

		private EventListenerDetails(final String componentID, final String portID,
				final IRuntimeEventListenerPort runtimeEventListenerPort)
		{
			this.componentID = componentID;
			this.portID = portID;
			this.runtimeEventListenerPort = runtimeEventListenerPort;
		}

		@Override
		public String toString()
		{
			return "EventListenerDetails(" + componentID + ":" + portID + "/" + runtimeEventListenerPort + ")";
		}
	}


	/**
	 * This method returns the value of the property specified by the key in the component 
	 * specified by the componentID.
	 * @param componentID the component whose property is to be returned
	 * @param key the key of the property whose value is to be returned
	 */
	public String getComponentProperty(String componentID, String key) {

		final IRuntimeModel runtimeModel = this.getCurrentRuntimeModel();
		final Set<IComponentInstance> componentInstanceSet =
				runtimeModel.getComponentInstances();

		for (IComponentInstance componentInstance : componentInstanceSet)
		{
			if (!componentInstance.getInstanceID().equals(componentID))
				continue;

			final String componentTypeID =componentInstance.getComponentTypeID();

			final IComponentType componentType = 
					componentRepository.getComponentType(componentTypeID);

			if (componentType == null)
			{
				return null;
			}
			//	final String canonicalName = componentType.getCanonicalName();
			final IRuntimeComponentInstance runtimeComponentInstance =
					runtimeComponentInstances.get(componentID);
			//Set runtime property values to component instances
			final Set<String> propertyNames = componentType.getPropertyNames();
			if (propertyNames != null)
			{
				for (final String propertyName : propertyNames)
				{


					if (propertyName.equals(key))
					{
						Object value = runtimeComponentInstance.
								getRuntimePropertyValue(key);

						return String.valueOf(value);

					}
				}
			}
		}
		return null;

	}


	/**
	 * This method sets the value of the property specified by the key in the component 
	 * specified by the componentID as specified by the value.
	 * @param componentID the component whose property is to be set
	 * @param key the key of the property whose value is to be set
	 * @param value the new value
	 */
	public void setComponentProperty(String componentID, String key,
			String value) {

		final IRuntimeModel runtimeModel = this.getCurrentRuntimeModel();
		final Set<IComponentInstance> componentInstanceSet =
				runtimeModel.getComponentInstances();
		for (IComponentInstance componentInstance : componentInstanceSet)
		{
			if (!componentInstance.getInstanceID().equals(componentID))
				continue;

			final String componentTypeID =
					componentInstance.getComponentTypeID();

			final IComponentType componentType = 
					componentRepository.getComponentType(componentTypeID);

			if (componentType == null)
			{
				return;
			}
			//final String canonicalName = componentType.getCanonicalName();
			final IRuntimeComponentInstance runtimeComponentInstance =
					runtimeComponentInstances.get(componentID);
			//Set runtime property values to component instances
			final Set<String> propertyNames = componentType.getPropertyNames();
			if (propertyNames != null)
			{
				for (final String propertyName : propertyNames)
				{

					if (propertyName.equals(key))
					{
						runtimeComponentInstance.
						setRuntimePropertyValue(key, value);
					}
				}
			}
		}
	}

	/**
	 * This method returns the component instance id of the specified component 
	 * if it exists or empty string otherwise.
	 * @param component the component whose instance is to be returned
	 */
	public String getComponentInstanceIDFromComponentInstance 
	(IRuntimeComponentInstance component) 
	{

		if (component==null) return "";
		Set<String> keys = runtimeComponentInstances.keySet();		
		for (IRuntimeComponentInstance c : 
			runtimeComponentInstances.values())
		{
			if (c.equals(component))
			{

				for (String k : keys)
				{
					if (runtimeComponentInstances.get(k).equals(component))
						return k;
				}	
			}
		}
		return "";
	}

	private void notifyAREEventListeners(AREEvent event) 
	{
		ArrayList<IAREEventListener> listeners = 
				AREServices.instance.getAREEventListners();
		
		for (IAREEventListener listener : listeners)
		{
			switch (event)
			{
			case PRE_DEPLOY_EVENT:
				listener.preDeployModel(); break;
			case POST_DEPLOY_EVENT:
				listener.postDeployModel(); break;
			case PRE_START_EVENT:
				listener.preStartModel(); break;
			case POST_STOP_EVENT:
				listener.postStopModel(); break;
			default:
				break;
			}
		}
		

/*		
		if (methodName.equals("preDeployModel"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.preDeployModel();
			}
		}
		else if (methodName.equals("postDeployModel"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.postDeployModel();
			}

		}
		else if (methodName.equals("preStartModel"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.preStartModel();
			}

		}
		else if (methodName.equals("postStopModel"))
		{
			for (IAREEventListener listener : listeners)
			{
				listener.postStopModel();
			}

		}
*/		

	}

	public void setGui(AstericsGUI gui) {

		this.gui = gui;

	}

	public void displayPanel(JPanel panel, 
			IRuntimeComponentInstance componentInstance, boolean display) 
	{

		Set<IComponentInstance> componentInstances = 
				getCurrentRuntimeModel().getComponentInstances();

		AREGUIElement ele;
		String id = getComponentInstanceIDFromComponentInstance(componentInstance);
		for (IComponentInstance instance : componentInstances)
		{
			if (instance.getInstanceID().equals(id))
			{
				ele = instance.getAREGUIElement();
				if (ele != null)
				{
					gui.displayPanel(panel, ele.posX, ele.posY, ele.width, 
							ele.height, display);
				}
			}
		}
	}
	
	/**
	 * @deprecated
	 * @param internalFrame
	 * @param display
	 */
	public void displayFrame (JInternalFrame internalFrame, boolean display)
	{
		gui.displayFrame(internalFrame, display);
	}

	public Dimension getAvailableSpace (IRuntimeComponentInstance componentInstance)
	{

		String componentInstanceID = 
				getComponentInstanceIDFromComponentInstance (componentInstance);

		IRuntimeModel model = getCurrentRuntimeModel();
		IComponentInstance component = 
				model.getComponentInstance(componentInstanceID);
		AREGUIElement el = component.getAREGUIElement();
		if (el!=null)
		{
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			int width = (int) (screenSize.width*el.width/10000f);
			int height = (int) (screenSize.height*el.height/10000f);
			return new Dimension( width , height );
		}
		else
			return new Dimension (0, 0);


	}

	public Point getComponentPosition (IRuntimeComponentInstance componentInstance)
	{

		String componentInstanceID = 
				getComponentInstanceIDFromComponentInstance (componentInstance);

		IRuntimeModel model = getCurrentRuntimeModel();
		IComponentInstance component = 
				model.getComponentInstance(componentInstanceID);
		AREGUIElement el = component.getAREGUIElement();

		if (el!=null)
		{
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			JPanel desktop = gui.getDesktop();
			//int x = ((screenSize.width*el.posX/100) + desktop.getLocationOnScreen().x);
			//int y = ((screenSize.height*el.posY/100) + desktop.getLocationOnScreen().y);
			int x = (int) ((screenSize.width*el.posX/10000f));
			int y = (int) ((screenSize.height*el.posY/10000f));

			return new Point( x , y );
		}
		else
			return new Point (0, 0);


	}

	public void setBundleManager(BundleManager bundleManager) {

		this.bundleManager = bundleManager;

	}


	public Collection<IRuntimeComponentInstance> getComponentRuntimeInstances ()
	{
		return runtimeComponentInstances.values();
	}

	/**
	 * Sync
	 * @param data
	 * @param portID
	 * @param targetComponentID
	 */
	public void bufferData(byte[] data, String portID, String targetComponentID)  
	{
		//		System.out.println ("Buffering..");
		//		System.out.println ("Data: "+data);
		//		System.out.println ("PortID: "+portID);
		//		System.out.println ("targetComponentID: "+targetComponentID);


		IComponentInstance ci = getCurrentRuntimeModel().
				getComponentInstance(targetComponentID);
		Set<IInputPort> bufferedPorts = ci.getBufferedInputPorts();
		Iterator<IInputPort> itr = bufferedPorts.iterator();

	
		if (!bufferedPortsMap.isEmpty() && 
				bufferedPortsMap.containsKey(targetComponentID)) //already buffering
		{

		

			Stack<HashMap<String, byte[]>> stack = bufferedPortsMap.get(targetComponentID);
			synchronized (stack)
			{
				if (stack.isEmpty()){

					
					
					HashMap<String, byte[]> row = new HashMap<String, byte[]>();
					synchronized (row)
					{
						row.put(portID, data);
						stack.push(row);
						
						
						
						if (row.size()==bufferedPorts.size()) //A row has been completed
						{
					
							
							IRuntimeComponentInstance rci = this.runtimeComponentInstances.get(targetComponentID);
							rci.syncedValuesReceived(row);
							stack.pop();
							if (stack.isEmpty()) bufferedPortsMap.remove(stack);
						}
					}
				}
				else {

			
					
					HashMap<String, byte[]> row = stack.peek();
					synchronized (row)
					{
						//if (row.size()<=100) 
						row.put(portID, data);
						if (row.size()==bufferedPorts.size()) //A row has been completed
						{
							IRuntimeComponentInstance rci = this.runtimeComponentInstances.get(targetComponentID);
							rci.syncedValuesReceived(row);
							stack.pop();
						}
					}
				}
			}
		}
		else //start buffering
		{
			Stack<HashMap<String, byte[]>> stack = new  Stack<HashMap<String, byte[]>>();
			synchronized (stack)
			{
				bufferedPortsMap.put(targetComponentID, stack);
				HashMap<String, byte[]> row = new HashMap<String, byte[]>();
				row.put(portID, data);
				stack.push(row);
				//	System.out.println("Row.zise="+row.size()+" bufferedPorts.size() "+bufferedPorts.size() );
				if (row.size()==bufferedPorts.size()) //A row has been completed
				{
				
					
					IRuntimeComponentInstance rci = runtimeComponentInstances.get(targetComponentID);
					rci.syncedValuesReceived(row);
					stack.pop();
					//if (stack.isEmpty()) bufferedPortsMap.remove(stack);
				}
			}
		}
	}
}
