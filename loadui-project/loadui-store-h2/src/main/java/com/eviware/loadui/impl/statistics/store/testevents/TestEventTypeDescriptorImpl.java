package com.eviware.loadui.impl.statistics.store.testevents;

import java.util.Map;
import java.util.Set;

import com.eviware.loadui.api.testevents.TestEventSourceDescriptor;
import com.eviware.loadui.api.testevents.TestEventTypeDescriptor;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

public class TestEventTypeDescriptorImpl implements TestEventTypeDescriptor
{
	private final String label;
	private final Map<String, TestEventSourceDescriptorImpl> sources = Maps.newHashMap();

	public TestEventTypeDescriptorImpl( String label )
	{
		this.label = label;
	}

	@Override
	public String getLabel()
	{
		return label;
	}

	@Override
	public Set<TestEventSourceDescriptor> getTestEventSources()
	{
		return ImmutableSet.<TestEventSourceDescriptor> copyOf( sources.values() );
	}

	public void putSource( String label, TestEventSourceDescriptorImpl source )
	{
		sources.put( label, source );
	}

	public TestEventSourceDescriptorImpl getSource( String label )
	{
		TestEventSourceDescriptorImpl source = sources.get( label );
		if( source == null )
		{
			sources.put( label, source = new TestEventSourceDescriptorImpl( label ) );
		}

		return source;
	}
}
