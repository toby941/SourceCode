package com.sourcecode.groovy;

import java.util.Random;

/**
 * @author jun.bao
 * @since 2013年9月2日
 */
public class GroovyIFoo implements IFoo {

	@Override
	public Object run(Object foo) {
		// TODO Auto-generated method stub
		Random r = new Random();
		return r.nextInt();
	}

}
