/*
 * Copyright 2013 SmartBear Software
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 */
package com.eviware.loadui.impl.summary.sections;

import com.eviware.loadui.api.model.CanvasItem;
import com.eviware.loadui.api.model.SceneItem;
import com.eviware.loadui.impl.model.ProjectItemImpl;
import com.eviware.loadui.impl.summary.MutableSectionImpl;
import com.eviware.loadui.util.summary.CalendarUtils;

public class ProjectDataSection extends MutableSectionImpl
{

	ProjectItemImpl project;

	public ProjectDataSection( ProjectItemImpl projectItemImpl )
	{
		super( "Project Data" );
		project = projectItemImpl;

		addValue( "Number of Scenarios", getNumberOfTestCases() );
		addValue( "Number of components", getNumberOfComponents() );
		addValue( "Number of project components", String.valueOf( project.getComponents().size() ) );
		addValue( "Time Limit", getLimit() );
		addValue( "Request Limit", getSampleLimit() );
		addValue( "Failure Limit", getFailureLimit() );
	}

	private String getNumberOfTestCases()
	{
		return String.valueOf( project.getChildren().size() );
	}

	private String getFailureLimit()
	{
		if( project.getLimit( CanvasItem.FAILURE_COUNTER ) > -1 )
			return String.valueOf( project.getLimit( CanvasItem.FAILURE_COUNTER ) );
		return "N/A";
	}

	private String getLimit()
	{
		if( project.getLimit( CanvasItem.TIMER_COUNTER ) > -1 )
			return CalendarUtils.formatInterval( project.getLimit( CanvasItem.TIMER_COUNTER ) * 1000 );
		return "N/A";
	}

	private String getNumberOfComponents()
	{
		int total = project.getComponents().size();
		for( SceneItem scene : project.getChildren() )
			total += scene.getComponents().size();
		return String.valueOf( total );
	}

	private String getSampleLimit()
	{
		if( project.getLimit( CanvasItem.SAMPLE_COUNTER ) > -1 )
			return String.valueOf( project.getLimit( CanvasItem.SAMPLE_COUNTER ) );
		return "N/A";
	}

}
