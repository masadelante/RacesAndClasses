/*******************************************************************************
 * Copyright 2014 Tobias Welther
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.tobiyas.racesandclasses.statistics;

import java.util.HashMap;
import java.util.Map;

import de.tobiyas.racesandclasses.traitcontainer.interfaces.markerinterfaces.Trait;


public class StatisticGatherer {

	/**
	 * The time the application started
	 */
	private final long timeStarted;
	
	private long eventsTriggeredTotal = 0;
	
	private Map<String, Long> traitsTriggersTotal = new HashMap<String, Long>();
	
	private Map<String, Long> traitTimeNeeded = new HashMap<String, Long>();
	
	
	public StatisticGatherer(long timeStarted) {
		this.timeStarted = timeStarted;
	}
	
	
	public void eventTriggered(){
		eventsTriggeredTotal ++;
	}
	
	/**
	 * Returns the time the system is running in MS
	 * @return
	 */
	public long getTimeRunning() {
		return System.currentTimeMillis() - timeStarted;
	}

	/**
	 * Returns the time the system is running as String
	 * @return
	 */
	public String getTimeRunningAsString(){
		long diffMSec = System.currentTimeMillis() - timeStarted;
		
		int left = 0;
	    int ss = 0;
	    int mm = 0;
	    int hh = 0;
	    int dd = 0;
	    left = (int) (diffMSec / 1000);
	    ss = left % 60;
	    left = (int) left / 60;
	    if (left > 0) {
	        mm = left % 60;
	        left = (int) left / 60;
	        if (left > 0) {
	            hh = left % 24;
	            left = (int) left / 24;
	            if (left > 0) {
	                dd = left;
	            }
	        }
	    }
	    
	    String diff = "days: " + Integer.toString(dd) + " hours: " + Integer.toString(hh) + " minutes: "
	            + Integer.toString(mm) + " seconds: " + Integer.toString(ss);
	    return diff;
	}
	
	/**
	 * @return the eventsTriggeredTotal
	 */
	public long getEventsTriggeredTotal() {
		return eventsTriggeredTotal;
	}


	/**
	 * @return the eventsModifiedTotal
	 */
	public Map<String, Long> getTraitsTriggersTotal() {
		return traitsTriggersTotal;
	}


	/**
	 * This should be called when a Trait is triggered.
	 * 
	 * @param trait
	 */
	public void traitTriggered(Trait trait){
		String traitName = trait.getName();
		
		if(!traitsTriggersTotal.containsKey(traitName)){
			traitsTriggersTotal.put(traitName, 1l);
		}else{
			long triggers = traitsTriggersTotal.get(traitName);
			traitsTriggersTotal.put(traitName, triggers + 1);
		}
	}


	/**
	 * Registers the time needed for this trait
	 * 
	 * @param name of the Trait
	 * @param timeNeeded time needed
	 */
	public void eventTime(String name, long timeNeeded) {
		if(traitTimeNeeded.containsKey(name)){
			timeNeeded += traitTimeNeeded.get(name);
		}
		
		traitTimeNeeded.put(name, timeNeeded);
	}

	
	/**
	 * Returns the times the Traits needed in total
	 * 
	 * @return the time traits needed
	 */
	public Map<String, Long> getTimeNeededTotal(){
		return traitTimeNeeded;
	}

}
