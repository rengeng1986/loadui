/*
 * Copyright 2011 eviware software ab
 * 
 * Licensed under the EUPL, Version 1.1 or - as soon they will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 * 
 * http://ec.europa.eu/idabc/eupl5
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the Licence for the specific language governing permissions and limitations
 * under the Licence.
 */
package com.eviware.loadui.api.charting;

import com.eviware.loadui.api.statistics.Statistic;
import com.eviware.loadui.api.statistics.StatisticVariable;
import com.eviware.loadui.util.StringUtils;

public class ChartNamePrettifier
{
	public static String nameFor( Statistic<?> statistic )
	{
		return nameForStatistic( statistic.getName() );
	}

	public static String nameForSource( String source )
	{
		return StatisticVariable.MAIN_SOURCE.equals( source ) ? "Total" : source;
	}

	public static String nameForStatistic( String statisticName )
	{
		statisticName = statisticName.replaceAll( "_", " " );
		statisticName = StringUtils.capitalizeEachWord( statisticName );
		if( statisticName.startsWith( "Percentile" ) )
		{
			statisticName = statisticName.replaceFirst( "Percent", "%-" );
		}

		statisticName = statisticName.replaceAll( "^Tps$", "TPS" ).replaceAll( "^Bps$", "BPS" );

		return statisticName;
	}
}
