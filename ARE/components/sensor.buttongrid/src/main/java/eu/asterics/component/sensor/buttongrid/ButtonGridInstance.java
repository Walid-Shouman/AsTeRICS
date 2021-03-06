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
 *    This project has been partly funded by the European Commission, 
 *                      Grant Agreement Number 247730
 *  
 *  
 *    License: GPL v3.0 (GNU General Public License Version 3.0)
 *                 http://www.gnu.org/licenses/gpl.html
 * 
 */

package eu.asterics.component.sensor.buttongrid;

import eu.asterics.mw.data.ConversionUtils;
import eu.asterics.mw.model.runtime.AbstractRuntimeComponentInstance;
import eu.asterics.mw.model.runtime.IRuntimeInputPort;
import eu.asterics.mw.model.runtime.IRuntimeOutputPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeOutputPort;
import eu.asterics.mw.model.runtime.IRuntimeEventTriggererPort;
import eu.asterics.mw.model.runtime.impl.DefaultRuntimeEventTriggererPort;
import eu.asterics.mw.services.AstericsErrorHandling;
import eu.asterics.mw.services.AREServices;


/**
 * Implements the screen keyboard using AsTeRICS GUI
 * 
 * @author Karol Pecyna [kpecyna@harpo.com.pl]
 *         Date: Mar 03, 2011
 *         Time: 9:49:00 AM
 */
public class ButtonGridInstance extends AbstractRuntimeComponentInstance
{
  final int NUMBER_OF_KEYS = 20;
  private final String PROP_CAPTION ="caption";
  private final String PROP_BUTTON_CAPTION="ButtonCaption";
  private final String PROP_BUTTON_CAPTION_VALUE="Button ";
  private final String PROP_HORIZONTAL_ORIENTATION="horizontalOrientation";
  private final String ETP_BUTTON = "button";
  private boolean guiActive=false;
  
  boolean propHorizontalOrientation=false; 
  
  private String propCaption="Keyboard";
  private String [] propKeyCaptionArray = new String[NUMBER_OF_KEYS];
  final EventPort [] etpKeyArray = new EventPort[NUMBER_OF_KEYS];
	
  private  GUI gui = null;
	
  /**
   * The class constructor.
   */  
  public ButtonGridInstance() 
  {
    for(int i=0;i<NUMBER_OF_KEYS;i++)
    {
      if(i<6)
      {
        propKeyCaptionArray[i]=PROP_BUTTON_CAPTION_VALUE+Integer.toString(i+1);
      }
      else
      {
        propKeyCaptionArray[i]="";
      }
	
        etpKeyArray[i]=new EventPort();
    		
    }
    	
  }

  /**
   * Returns an Input Port.
   * @param portID   the name of the port
   * @return         the input port or null if not found
   */
  public IRuntimeInputPort getInputPort(String portID)
  {
    return null;
  }

  /**
   * Returns an Output Port.
   * @param portID   the name of the port
   * @return         the output port
   */    
  public IRuntimeOutputPort getOutputPort(String portID)
  {   
    return null;
  }
    

  /**
   * Returns the value of the given property.
   * @param propertyName   the name of the property
   * @return               the property value or null if not found
   */ 
  public Object getRuntimePropertyValue(String propertyName)
  {
    if(PROP_CAPTION.equalsIgnoreCase(propertyName))
    {
      return propCaption;
    		
    }
    else  if(PROP_HORIZONTAL_ORIENTATION.equalsIgnoreCase(propertyName))
    {
      return propHorizontalOrientation;
      		
    }
    else
    {
      int propKeyCaptionSize=PROP_BUTTON_CAPTION.length();
      if(propertyName.length()>propKeyCaptionSize)
      {
        String testName=propertyName.substring(0,propKeyCaptionSize);
    	if(testName.equalsIgnoreCase(PROP_BUTTON_CAPTION))
    	{
    	  String captionNumberText=propertyName.substring(propKeyCaptionSize);
    	  int captionNumberValue;
    	    	  
    	  try
    	  {
    	    captionNumberValue = Integer.parseInt(captionNumberText);
    	  }
    	  catch(NumberFormatException ex)
    	  {
    	    return null;
    	  }
    	    			
    	  if(captionNumberValue>0 && captionNumberValue<=NUMBER_OF_KEYS)
    	  {
    	    return propKeyCaptionArray[captionNumberValue-1];
    	  }
    	  else
    	  {
    	    return null;
    	  }
        }
      }
   
      return null;
    }     	
       
  }
 
  /**
   * Sets a new value for the given property.
   * @param propertyName   the name of the property
   * @param newValue       the desired property value
   */
  public Object setRuntimePropertyValue(String propertyName, Object newValue)
  {
    if(PROP_CAPTION.equalsIgnoreCase(propertyName))
    {
      final String oldValue = propCaption;
           
      propCaption=(String)newValue;
            
      return oldValue;
    }
    else if(PROP_HORIZONTAL_ORIENTATION.equalsIgnoreCase(propertyName))
    {
    	final Object oldValue = propHorizontalOrientation;
        if("true".equalsIgnoreCase((String)newValue))
        {
        	propHorizontalOrientation = true;
        }
        else if("false".equalsIgnoreCase((String)newValue))
        {
        	propHorizontalOrientation = false;
        }
        return oldValue;
    }
    else
    {
      int propKeyCaptionSize=PROP_BUTTON_CAPTION.length();
      if(propertyName.length()>propKeyCaptionSize)
      {
        String testName=propertyName.substring(0,propKeyCaptionSize);
    	if(testName.equalsIgnoreCase(PROP_BUTTON_CAPTION))
    	{
    	  String captionNumberText=propertyName.substring(propKeyCaptionSize);
    	  int captionNumberValue;
    	    	  
    	  try
    	  {
    	    captionNumberValue = Integer.parseInt(captionNumberText);
    	  }
    	  catch(NumberFormatException ex)
    	  {
    	     return null;
    	  }
    	    			
    	  if(captionNumberValue>0 && captionNumberValue<=NUMBER_OF_KEYS)
    	  {    	    	    
    	    final String oldValue = propKeyCaptionArray[captionNumberValue-1];         
    	    propKeyCaptionArray[captionNumberValue-1]=(String)newValue;
    	    return oldValue;
    	  }
    	  else
    	  {
    	    return null;
    	  }
    	}
      }
      
      return null;
    }        	
        
  }

  /**
   * Returns an Event Trigger Port.
   * @param eventPortID   the name of the port
   * @return         the Event Trigger port or null if not found
   */
  public IRuntimeEventTriggererPort getEventTriggererPort(String eventPortID)
  {
    int elpKeySize=ETP_BUTTON.length();
	if(eventPortID.length()>elpKeySize)
	{
	  String testName=eventPortID.substring(0,elpKeySize);
	  if(testName.equalsIgnoreCase(ETP_BUTTON))
	  {
	    String keyNumberText=eventPortID.substring(elpKeySize);
	    int keyNumberValue;
	    	  
	    try
	    {
	      keyNumberValue = Integer.parseInt(keyNumberText);
	    }
	    catch(NumberFormatException ex)
	    {
	      return null;
	    }
	    			
	    if(keyNumberValue>0 && keyNumberValue<=NUMBER_OF_KEYS)
	    {
	      return etpKeyArray[keyNumberValue-1];
	    }
	    else
	    {
	      return null;
	    }
	  }
	}
	    
	return null;
        
  }
    
  /**
   * Returns the plugin caption.
   * @return   plugin caption
   */
  String getCaption()
  {
    return propCaption;
  }

  /**
   * Returns the button caption.
   * @param    button index
   * @return   button caption
   */
  String getButtonCaption(int i)
  {
    return propKeyCaptionArray[i];
  }
  
  /**
   * Called when model is started.
   */
  @Override
  public void start()
  {
    gui = new GUI(this,AREServices.instance.getAvailableSpace(this));
    AREServices.instance.displayPanel(gui, this, true);
    super.start();
  }

  /**
   * Called when model is paused.
   */
  @Override
  public void pause()
  {
    super.pause();
  }
  
  
  /**
   * Called when model is resumed.
   */
  @Override
  public void resume()
  {
    super.resume();
  }

  /**
   * Called when model is stopped
   */
  @Override
  public void stop()
  {
    AREServices.instance.displayPanel(gui, this, false);
    super.stop();
  }

  /**
   * Event generated by button press
   */
  public class EventPort extends DefaultRuntimeEventTriggererPort
  {
    public void raiseEvent()
    {
      super.raiseEvent();
    }
  }

}