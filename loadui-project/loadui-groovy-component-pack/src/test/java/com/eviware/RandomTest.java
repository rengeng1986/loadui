package com.eviware;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.eviware.loadui.api.component.ComponentCreationException;
import com.eviware.loadui.api.component.categories.GeneratorCategory;
import com.eviware.loadui.api.model.ComponentItem;
import com.eviware.loadui.api.terminal.InputTerminal;
import com.eviware.loadui.api.terminal.OutputTerminal;
import com.eviware.loadui.groovy.util.GroovyComponentTestUtils;
import com.google.common.base.Joiner;

public class RandomTest
{
	private ComponentItem component;

	@BeforeClass
	public static void classSetup()
	{
		GroovyComponentTestUtils.initialize( Joiner.on( File.separator ).join( "src", "main", "groovy" ) );
	}

	@Before
	public void setup() throws ComponentCreationException
	{
		GroovyComponentTestUtils.getDefaultBeanInjectorMocker();
		component = GroovyComponentTestUtils.createComponent( "Random" );
	}

	@Test
	public void shouldHaveCorrectTerminals()
	{
		assertThat( component.getTerminals().size(), is( 2 ) );

		InputTerminal incoming = ( InputTerminal )component.getTerminalByName( GeneratorCategory.STATE_TERMINAL );
		assertThat( incoming.getLabel(), is( "Component activation" ) );

		OutputTerminal trigger = ( OutputTerminal )component.getTerminalByName( GeneratorCategory.TRIGGER_TERMINAL );
		assertThat( trigger.getLabel(), is( "Trigger Signal" ) );
	}
}
