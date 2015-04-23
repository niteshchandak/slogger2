/**
 * 
 */
package org.slogger.provider.randomMessageProvider;

/**
 * @author preetham
 *
 */
public class FixedValueProvider implements IValueProvider {
	
	private Object value;

	public FixedValueProvider(Object value) {
		this.value = value;
	}

	public Object getValue() {	
		return value;
	}

}
