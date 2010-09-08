// 
// Copyright 2010 eviware software ab
// 
// Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent
// versions of the EUPL (the "Licence");
// You may not use this work except in compliance with the Licence.
// You may obtain a copy of the Licence at:
// 
// http://ec.europa.eu/idabc/eupl5
// 
// Unless required by applicable law or agreed to in writing, software distributed under the Licence is
// distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
// express or implied. See the Licence for the specific language governing permissions and limitations
// under the Licence.
// 

/**
 * Checks for errors and increases global assertion count
 * 
 * @help http://www.loadui.org/Analysis/assertion-component.html
 * @category analysis
 * @nonBlocking true
 */

import java.util.LinkedList

import com.eviware.loadui.api.events.PropertyEvent
import com.eviware.loadui.api.events.ActionEvent
import com.eviware.loadui.util.layout.DelayedFormattedString
import com.eviware.loadui.impl.layout.OptionsProviderImpl

import com.eviware.loadui.api.model.CanvasItem;

createOutput( 'output', 'Output for failed messages' )

def componentSignature = [
		"Assert" : String.class,
		"Min" : Long.class,
		"Value" : Long.class,
		"Max" : Long.class,
		"Period" : Long.class,
		"Tolerance" : Long.class,
		"Timestamp" : Long.class
		]
setSignature(output, componentSignature)

failureCounter = getCounter( CanvasItem.FAILURE_COUNTER );
totalCounter = getCounter( CanvasItem.ASSERTION_COUNTER );

//Properties
createProperty( 'value', String, "Select value" )
createProperty( 'min', Long, 0 )
createProperty( 'max', Long, 1000 )
createProperty( 'tolerance', Long, 1 )
createProperty( 'period', Long, 0 )

createProperty( 'sampleId', String, "" )
createProperty( 'failOnMissingID', Boolean, false )
createProperty( 'failOnMissingValue', Boolean, false )

String valueToAssert = value.value

buffer = new LinkedList()

outMsg = newMessage()

OptionsProviderImpl provider = new OptionsProviderImpl([])

assertedResetValue = 0
failedResetValue = 0

assertedDisplay = new DelayedFormattedString( '%d', 500, value { totalCounter.get()-assertedResetValue } )
failedDisplay = new DelayedFormattedString( '%d', 500, value { failureCounter.get()-failedResetValue } )

onConnect = { outgoing, incoming ->
	if( incoming == inputTerminal ){
		updateProviders()
	}
}

onDisconnect = { outgoing, incoming ->
	if( incoming == inputTerminal ){
		updateProviders()
	}
}

updateProviders = {
	def options = []
	for( conn in inputTerminal.connections ) {
		for( key in conn.outputTerminal.messageSignature.keySet() )
			options += key
	}
	provider.options = options
	if( !options.contains(value.value) )
		value.value = 'Select value'
}

onSignature = { outgoing, signature ->
	updateProviders()
}

analyze = { message ->
	try{
		long timestamp = System.currentTimeMillis()
		
		if(!message.containsKey(value.value)) {
			if( failOnMissingValue.value ) {
				raiseFailure(timestamp, val)
				totalCounter.increment()
			}
			return
		}
		
		if( sampleId.value ?: "" != "" && message[SampleCategory.SAMPLE_ID] != sampleId.value ) {
			if( failOnMissingID.value ) {
				raiseFailure(timestamp, val)
				totalCounter.increment()
			}
			return
		}
		
		double val = message[value.value]
		
		if( val < min.value || val > max.value ) {
			synchronized( buffer ) {
				buffer.addLast( timestamp )
				if( buffer.size() >= tolerance.value ) {
					def lit = buffer.listIterator()
					long entry = null
					//Remove old entries
					if( period.value > 0 ) {
						long oldest = timestamp - period.value * 1000
						while( lit.hasNext() ) if( lit.next() < oldest ) lit.remove()
					}
				}
				
				if( buffer.size() >= tolerance.value ) {
					raiseFailure(timestamp, val)
					buffer.clear()
				}
			}
		}
		
		totalCounter.increment()
	}
	catch(Exception e){
		ex(e, "Assertion -> analyze")
	}
}

raiseFailure = {timestamp, value ->
	failureCounter.increment()
	
	outMsg["Assert"] = valueToAssert
	outMsg["Min"] = min.value
	outMsg["Value"] = value
	outMsg["Max"] = max.value
	outMsg["Period"] = period.value
	outMsg["Tolerance"] = tolerance.value
	outMsg["Timestamp"] = timestamp
	send(output, outMsg)
}

onRelease = { 
	assertedDisplay.release()
	failedDisplay.release()
}

resetComponent = {
	buffer.clear()
	assertedResetValue = 0
	failedResetValue = 0
}

addEventListener( PropertyEvent ) { event ->
	if ( event.event == PropertyEvent.Event.VALUE ) {
		if( event.property == value ) {
			valueToAssert = value.value
			resetComponent()
		}
		else if( event.property == min ) {
			if(max.value < min.value){
				max.value = min.value
			}
		}
	}
}

addEventListener( ActionEvent ) { event ->
	if ( event.key == "RESET" ) {
		resetComponent()
	}
}

ex = {t, m ->
	println("-------------------------------")
	println("exception in ${m} method in groovy")
	println("type: ${t}")
	println("message: ${t.getMessage()}")
	println("stacktrace:")
	int tCnt = 0
	for(item in t.getStackTrace()){
		if(tCnt == 0){
			println("${item}")
		}
		else{
			println("\t${item}")
		}
		tCnt++
	}
	println("-------------------------------")
}

//Layout
layout {
	property( property: value, widget: 'comboBox', label: 'Value', options: provider, constraints: 'w 100!' )
	separator( vertical: true )
//	box {
		property( property: min, label: 'Min', min: 0 )
		property( property: max, label: 'Max', min: 0 )
//	}
	separator( vertical: true )
//	box {
//		property( property: tolerance, label: 'Tolerance', min: 1, constraints:'wrap 1')
//		property( property: period, label: 'Period', min: 0 )
//	}
//	separator( vertical: true )
	box(layout:'wrap, ins 0') {
		box( widget:'display' ) {
			node( label:'Asserted', fString: assertedDisplay, constraints:'w 60!' )
			node( label:'Failed', fString: failedDisplay, constraints:'w 60!' )
		}
		action( 
			label: 'Reset', 
			action: {
				buffer.clear()
				assertedResetValue = totalCounter.get()
				failedResetValue = failureCounter.get()
			}, 
			constraints:'align right'
		)
	}
}

//Compact Layout
compactLayout {
	box( widget:'display' ) {
		node( label:'Asserted', fString: assertedDisplay )
		node( label:'Failed', fString: failedDisplay )
	}
}

settings( label: "General" ) {
	property( property: tolerance, label: 'Tolerance', min: 1)
	property( property: period, label: 'Period' )
	property(property: failOnMissingValue, label: 'Fail on missing value' )
	property(property: sampleId, label: 'Sample ID' )
	property(property: failOnMissingID, label: 'Fail on mismatching ID' )
}