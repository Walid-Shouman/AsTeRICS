

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
 *       This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: LGPL v3.0 (GNU Lesser General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/lgpl.html
 * 
 */

package eu.asterics.component.actuator.fS20Sender;


import java.net.URL;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventListenerPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.*;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;
import eu.asterics.component.actuator.fS20Sender.PCSDevice;
import eu.asterics.component.actuator.fS20Sender.FS20Utils;
//import eu.asterics.component.actuator.fS20Sender.PCSDevice;
//import eu.asterics.component.actuator.fS20Sender.FS20Utils;

/**
 * 
 * <Describe purpose of this module>
 * 
 * 
 *  
 * @author David Thaller [dt@ki-i.at]
 *         Date: 
 *         Time: 
 */


public class FS20SenderInstance extends AbstractRuntimeComponentInstance
{
	
	static {
		System.loadLibrary("hidapi-jni");
	}
	// Usage of an output port e.g.: opMyOutPort.sendData(ConversionUtils.intToBytes(10)); 

	// Usage of an event trigger port e.g.: etpMyEtPort.raiseEvent();

	int propHousecode = 11111111;
	int propAddress = 1111;

	
	// declare member variables here

	private int houseCode = 11111111;
	private int address = 1111;
	private PCSDevice pcs;
    
   /**
    * The class constructor.
    */
    public FS20SenderInstance()
    {
    	
    }

   /**
    * returns an Input Port.
    * @param portID   the name of the port
    * @return         the input port or null if not found
    */
    public IRuntimeInputPort getInputPort(String portID)
    {
		if ("housecode".equalsIgnoreCase(portID))
		{
			return ipHousecode;
		}
		if ("address".equalsIgnoreCase(portID))
		{
			return ipAddress;
		}
		if ("action".equalsIgnoreCase(portID))
		{
			return ipAction;
		}

		return null;
	}

    /**
     * returns an Output Port.
     * @param portID   the name of the port
     * @return         the output port or null if not found
     */
    public IRuntimeOutputPort getOutputPort(String portID)
	{

		return null;
	}

    /**
     * returns an Event Listener Port.
     * @param eventPortID   the name of the port
     * @return         the EventListener port or null if not found
     */
    public IRuntimeEventListenerPort getEventListenerPort(String eventPortID)
    {
		if ("off".equalsIgnoreCase(eventPortID))
		{
			return elpOff;
		}
		if ("onLevel1".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel1;
		}
		if ("onLevel2".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel2;
		}
		if ("onLevel3".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel3;
		}
		if ("onLevel4".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel4;
		}
		if ("onLevel5".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel5;
		}
		if ("onLevel6".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel6;
		}
		if ("onLevel7".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel7;
		}
		if ("onLevel8".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel8;
		}
		if ("onLevel9".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel9;
		}
		if ("onLevel10".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel10;
		}
		if ("onLevel11".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel11;
		}
		if ("onLevel12".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel12;
		}
		if ("onLevel13".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel13;
		}
		if ("onLevel14".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel14;
		}
		if ("onLevel15".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel15;
		}
		if ("onLevel16".equalsIgnoreCase(eventPortID))
		{
			return elpOnLevel16;
		}
		if ("onOldLevel".equalsIgnoreCase(eventPortID))
		{
			return elpOnOldLevel;
		}
		if ("toggle".equalsIgnoreCase(eventPortID))
		{
			return elpToggle;
		}
		if ("dimUp".equalsIgnoreCase(eventPortID))
		{
			return elpDimUp;
		}
		if ("dimDown".equalsIgnoreCase(eventPortID))
		{
			return elpDimDown;
		}
		if ("dimUpAndDown".equalsIgnoreCase(eventPortID))
		{
			return elpDimUpAndDown;
		}
		if ("programTimer".equalsIgnoreCase(eventPortID))
		{
			return elpProgramTimer;
		}
		if ("offForTimerThenOldLevel".equalsIgnoreCase(eventPortID))
		{
			return elpOffForTimerThenOldLevel;
		}
		if ("onForTimerThenOff".equalsIgnoreCase(eventPortID))
		{
			return elpOnForTimerThenOff;
		}
		if ("onOldLevelForTimerThenOff".equalsIgnoreCase(eventPortID))
		{
			return elpOnOldLevelForTimerThenOff;
		}
		if ("reset".equalsIgnoreCase(eventPortID))
		{
			return elpReset;
		}
		if ("onForTimerThenOldLevel".equalsIgnoreCase(eventPortID))
		{
			return elpOnForTimerThenOldLevel;
		}
		if ("onOldLevelForTimerThenPreviousState".equalsIgnoreCase(eventPortID))
		{
			return elpOnOldLevelForTimerThenPreviousState;
		}

        return null;
    }

    /**
     * returns an Event Triggerer Port.
     * @param eventPortID   the name of the port
     * @return         the EventTriggerer port or null if not found
     */
    public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
    {

        return null;
    }
		
    /**
     * returns the value of the given property.
     * @param propertyName   the name of the property
     * @return               the property value or null if not found
     */
    public Object getRuntimePropertyValue(String propertyName)
    {
		if ("housecode".equalsIgnoreCase(propertyName))
		{
			return propHousecode;
		}
		if ("address".equalsIgnoreCase(propertyName))
		{
			return propAddress;
		}

        return null;
    }

    /**
     * sets a new value for the given property.
     * @param propertyName   the name of the property
     * @param newValue       the desired property value or null if not found
     */
    public Object setRuntimePropertyValue(String propertyName, Object newValue)
    {
		if ("housecode".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propHousecode;
			propHousecode = Integer.parseInt(newValue.toString());
			return oldValue;
		}
		if ("address".equalsIgnoreCase(propertyName))
		{
			final Object oldValue = propAddress;
			propAddress = Integer.parseInt(newValue.toString());
			return oldValue;
		}

        return null;
    }

     /**
      * Input Ports for receiving values.
      */
	private final IRuntimeInputPort ipHousecode  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				 houseCode = ConversionUtils.intFromBytes(data); 
		}
	};
	private final IRuntimeInputPort ipAddress  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{ 
				 address = ConversionUtils.intFromBytes(data); 
		}
	};
	private final IRuntimeInputPort ipAction  = new DefaultRuntimeInputPort()
	{
		public void receiveData(byte[] data)
		{
				String action = ConversionUtils.stringFromBytes(data);
				
				/*
				 * Parse the action string! If it has a valid format send the corresponding command to the specified device
				 * Format is as follows:
				 *  hc_address_command
				 *  example:
				 *  11111112_1234_18 would send the toggle command with housecode=11111112 and address=1234
				 */
				String [] values = action.split("_");
				if (values.length != 3) {
					AstericsErrorHandling.instance.reportError(new FS20SenderInstance(), "Parameter mismatch for action string "+action+"! Format is: hc_addr_cmd! example: 11111111_1111_18 for toggle");
					return;
				}
				String [] prefix = values[0].split(":");
				if ((prefix.length != 2) || ( ! prefix[0].equals("@FS20"))) {
					AstericsErrorHandling.instance.reportError(new FS20SenderInstance(), "Parameter mismatch for action string "+action+"! Format is: hc_addr_cmd! example: 11111111_1111_18 for toggle");
					return;
				}
				try {
					int hc = Integer.parseInt(prefix[1]);  // this is the housecode
					int a = Integer.parseInt(values[1]);
					int cmd = Integer.parseInt(values[2]);
					if (pcs != null)
						pcs.send(hc, a, cmd);
				} catch (NumberFormatException ne) {
					AstericsErrorHandling.instance.reportError(new FS20SenderInstance(), "Parameter mismatch for action string "+action+"! Format is: hc_addr_cmd! example: 11111111_1111_18 for toggle");
				}
		}
	};


     /**
      * Event Listerner Ports.
      */
	final IRuntimeEventListenerPort elpOff = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
				 if (pcs != null)
					 pcs.send(houseCode, address, FS20Utils.Off);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel1 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep1);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel2 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep2);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel3 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep3);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel4 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep4);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel5 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep5);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel6 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep6);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel7 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep7);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel8 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep8);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel9 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep9);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel10 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep10);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel11 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				pcs.send(houseCode, address, FS20Utils.OnStep11);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel12 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep12); 
		}
	};
	final IRuntimeEventListenerPort elpOnLevel13 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep13); 
		}
	};
	final IRuntimeEventListenerPort elpOnLevel14 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
					 pcs.send(houseCode, address, FS20Utils.OnStep14);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel15 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep15);
		}
	};
	final IRuntimeEventListenerPort elpOnLevel16 = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnStep16);
		}
	};
	final IRuntimeEventListenerPort elpOnOldLevel = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnOld);
		}
	};
	final IRuntimeEventListenerPort elpToggle = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.Toggle);
		}
	};
	final IRuntimeEventListenerPort elpDimUp = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.DimUp);
		}
	};
	final IRuntimeEventListenerPort elpDimDown = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.DimDown);
		}
	};
	final IRuntimeEventListenerPort elpDimUpAndDown = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.DimUpDown);
		}
	};
	final IRuntimeEventListenerPort elpProgramTimer = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.TimeSet);
		}
	};
	final IRuntimeEventListenerPort elpOffForTimerThenOldLevel = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OffForTimeOld);
		}
	};
	final IRuntimeEventListenerPort elpOnForTimerThenOff = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnForTimeOff);
		}
	};
	final IRuntimeEventListenerPort elpOnOldLevelForTimerThenOff = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnOldForTimeOff);
		}
	};
	final IRuntimeEventListenerPort elpReset = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.Reset);
		}
	};
	final IRuntimeEventListenerPort elpOnForTimerThenOldLevel = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnForTimeOld);
		}
	};
	final IRuntimeEventListenerPort elpOnOldLevelForTimerThenPreviousState = new IRuntimeEventListenerPort()
	{
		public void receiveEvent(final String data)
		{
			if (pcs != null)
				 pcs.send(houseCode, address, FS20Utils.OnOldForTimeOld);
		}
	};

	

     /**
      * called when model is started.
      */
      @Override
      public void start()
      {
    	  super.start();
    	  openDevice();
    	  houseCode = propHousecode;
    	  address = propAddress;
      }

     /**
      * called when model is paused.
      */
      @Override
      public void pause()
      {
          closeDevice();
          super.pause();
      }

     /**
      * called when model is resumed.
      */
      @Override
      public void resume()
      {
          super.resume();
          openDevice();
      }

     /**
      * called when model is stopped.
      */
      @Override
      public void stop()
      {
          closeDevice();
          super.stop();
      }
      
      /**
       * Open the USB HID Device
       */
      private void openDevice() {
    	  pcs = new PCSDevice();
    	  if (!pcs.open()) {
    		  AstericsErrorHandling.instance.reportError(this, "Could not open/find PCS Device");
    		  pcs = null;
    		  return;
    	  }
      }
      
      /**
       * Close the USB HID Device
       */
      private void closeDevice() {
    	  if (pcs != null) {
	    	  if (!pcs.close()) {
	    		  AstericsErrorHandling.instance.reportError(this, "Could not close PCS Device");
	    		  pcs = null;
	    		  return;
	    	  }
    	  }
      }
      
}