/*******************************************************************************
 * Copyright (c) 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.core.tests.internal.dynamicregistry;

import java.io.IOException;
import java.util.Arrays;
import junit.framework.TestCase;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.tests.harness.BundleTestingHelper;
import org.eclipse.core.tests.runtime.RuntimeTestsPlugin;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;

public class ExtensionRegistryStaticTest extends TestCase {

	public void testA() throws IOException, BundleException {
		//test the addition of an extension point
		String name = "A";
		Bundle bundle01 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/test" + name);
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle01});
		testExtensionPoint(name);
	}
	
	public void testAFromCache() {
		//Check that it has been persisted
		testExtensionPoint("A");
	}
	
	private void testExtensionPoint(String name) {
		assertNotNull(Platform.getExtensionRegistry().getExtensionPoint("test" + name + ".xpt" + name));
		assertEquals(Platform.getExtensionRegistry().getExtensionPoint("test" + name + ".xpt" + name).getLabel(), "Label xpt" + name);
		assertEquals(Platform.getExtensionRegistry().getExtensionPoint("test" + name + ".xpt" + name).getNamespace(), "test" + name);
		assertEquals(Platform.getExtensionRegistry().getExtensionPoint("test" + name + ".xpt" + name).getSchemaReference(), "schema/xpt" + name + ".exsd");			
	}
	
	public void testB() throws IOException, BundleException {
		//test the addition of an extension without extension point
		Bundle bundle01 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testB/1");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle01});
		assertNull(Platform.getExtensionRegistry().getExtension("testB2", "xptB2", "ext1"));
	}
	
	public void testBFromCache()  throws IOException, BundleException {
		// Test the addition of an extension point when orphans extension exists 
		assertNull(Platform.getExtensionRegistry().getExtension("testB2", "xptB2", "ext1"));
		Bundle bundle02 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testB/2");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle02});
		testExtensionPoint("B2");
		
		//	Test the configuration elements
		assertEquals(Platform.getExtensionRegistry().getExtension("testB2", "xptB2", "testB1.ext1").getConfigurationElements().length, 0);
		assertEquals(Platform.getExtensionRegistry().getConfigurationElementsFor("testB2.xptB2").length, 0);
		
		//Test the number of extension in the namespace
		assertEquals(Platform.getExtensionRegistry().getExtensions("testB1").length, 1);
		
		//Test the extension
		assertNotNull(Platform.getExtensionRegistry().getExtension("testB1.ext1"));
		assertNotNull(Platform.getExtensionRegistry().getExtension("testB2", "xptB2", "testB1.ext1"));
		assertNotNull(Platform.getExtensionRegistry().getExtension("testB2.xptB2", "testB1.ext1"));
		
		assertNotNull(Platform.getExtensionRegistry().getExtensionPoint("testB2.xptB2").getExtension("testB1.ext1"));
		assertEquals(Platform.getExtensionRegistry().getExtensionPoint("testB2.xptB2").getExtensions()[0].getUniqueIdentifier(), "testB1.ext1");
		
		//uninstall the bundle contributing the extension point
		bundle02.uninstall();
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle02} );
		
		assertNull(Platform.getExtensionRegistry().getExtension("testB1.ext1"));
		assertEquals(Platform.getExtensionRegistry().getExtensions("testB1").length, 0);
		assertEquals(Platform.getExtensionRegistry().getExtensionPoints("testB2").length, 0);
		assertNull(Platform.getExtensionRegistry().getExtensionPoint("testB2.xptB2"));
	}
	
	public void testBRemoved() {
		//Test if testB has been removed.
		assertNull(Platform.getExtensionRegistry().getExtension("testB1.ext1"));
		assertEquals(Platform.getExtensionRegistry().getExtensions("testB1").length, 0);
		assertEquals(Platform.getExtensionRegistry().getExtensionPoints("testB2").length, 0);
		assertNull(Platform.getExtensionRegistry().getExtensionPoint("testB2.xptB2"));
	}
	
	public void testC() throws IOException, BundleException {
		//test the addition of an extension point then the addition of an extension
		Bundle bundle01 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testC/1");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle01});
		testExtensionPoint("C1");
		Bundle bundle02 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testC/2");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle02});
		
		//Test the configurataion elements
		assertEquals(Platform.getExtensionRegistry().getExtension("testC1", "xptC1", "testC2.ext1").getConfigurationElements().length, 0);
		assertEquals(Platform.getExtensionRegistry().getConfigurationElementsFor("testC1.xptC1").length, 0);
		
		//Test the number of extension in the namespace
		assertEquals(Platform.getExtensionRegistry().getExtensions("testC2").length, 1);
		
		//Test the extension
		assertNotNull(Platform.getExtensionRegistry().getExtension("testC2.ext1"));
		assertNotNull(Platform.getExtensionRegistry().getExtension("testC1", "xptC1", "testC2.ext1"));
		assertNotNull(Platform.getExtensionRegistry().getExtension("testC1.xptC1", "testC2.ext1"));
		
		assertNotNull(Platform.getExtensionRegistry().getExtensionPoint("testC1.xptC1").getExtension("testC2.ext1"));
		assertEquals(Platform.getExtensionRegistry().getExtensionPoint("testC1.xptC1").getExtensions()[0].getUniqueIdentifier(), "testC2.ext1");
	}
	
	public void testD() throws IOException, BundleException {
		//test the addition of an extension then the addition of an extension point
		Bundle bundle02 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testD/2");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle02});
		Bundle bundle01 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testD/1");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle01});
		testExtensionPoint("D1");

		//Test the configurataion elements
		assertEquals(Platform.getExtensionRegistry().getExtension("testD1", "xptD1", "testD2.ext1").getConfigurationElements().length, 0);
		assertEquals(Platform.getExtensionRegistry().getConfigurationElementsFor("testD1.xptD1").length, 0);
		
		//Test the number of extension in the namespace
		assertEquals(Platform.getExtensionRegistry().getExtensions("testD2").length, 1);
		
		//Test the extension
		assertNotNull(Platform.getExtensionRegistry().getExtension("testD2.ext1"));
		assertNotNull(Platform.getExtensionRegistry().getExtension("testD1", "xptD1", "testD2.ext1"));
		assertNotNull(Platform.getExtensionRegistry().getExtension("testD1.xptD1", "testD2.ext1"));
		
		assertNotNull(Platform.getExtensionRegistry().getExtensionPoint("testD1.xptD1").getExtension("testD2.ext1"));
		assertEquals(Platform.getExtensionRegistry().getExtensionPoint("testD1.xptD1").getExtensions()[0].getUniqueIdentifier(), "testD2.ext1");
	}
	
	public void testE() throws IOException, BundleException {
		//test the addition of an extension point and then add the extension through a fragment
		Bundle bundle01 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testE/1");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle01});
		Bundle bundle02 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testE/2");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle02});
		testExtensionPoint("E1");
		
		//Test the configurataion elements
		assertEquals(Platform.getExtensionRegistry().getExtension("testE1", "xptE1", "testE1.ext1").getConfigurationElements().length, 0);
		assertEquals(Platform.getExtensionRegistry().getConfigurationElementsFor("testE1.xptE1").length, 0);
		
		//Test the number of extension in the namespace
		assertEquals(Platform.getExtensionRegistry().getExtensions("testE1").length, 1);
		
		//Test the extension
		assertNotNull(Platform.getExtensionRegistry().getExtension("testE1", "xptE1", "testE1.ext1"));
		assertNotNull(Platform.getExtensionRegistry().getExtension("testE1.xptE1", "testE1.ext1"));
		assertNotNull(Platform.getExtensionRegistry().getExtension("testE1.ext1"));  //This test exhibits a bug in the 3.0 implementation
		
		assertNotNull(Platform.getExtensionRegistry().getExtensionPoint("testE1.xptE1").getExtension("testE1.ext1"));
		assertEquals(Platform.getExtensionRegistry().getExtensionPoint("testE1.xptE1").getExtensions()[0].getUniqueIdentifier(), "testE1.ext1");
	}
	
	public void testF() throws IOException, BundleException {
		//test the addition of the extension through a fragment then the addition of an extension point 
		Bundle bundle02 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testF/2");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle02});
		Bundle bundle01 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testF/1");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle01});
		testExtensionPoint("F1");
		
		//Test the configurataion elements
		assertEquals(Platform.getExtensionRegistry().getExtension("testF1", "xptF1", "testF1.ext1").getConfigurationElements().length, 0);
		assertEquals(Platform.getExtensionRegistry().getConfigurationElementsFor("testF1.xptE1").length, 0);
		
		//Test the number of extension in the namespace
		assertEquals(Platform.getExtensionRegistry().getExtensions("testF1").length, 1);
		
		//Test the extension
		assertNotNull(Platform.getExtensionRegistry().getExtension("testF1", "xptF1", "testF1.ext1"));
		assertNotNull(Platform.getExtensionRegistry().getExtension("testF1.xptF1", "testF1.ext1"));
		assertNotNull(Platform.getExtensionRegistry().getExtension("testF1.ext1"));  //This test exhibits a bug in the 3.0 implementation
		
		assertNotNull(Platform.getExtensionRegistry().getExtensionPoint("testF1.xptF1").getExtension("testF1.ext1"));
		assertEquals(Platform.getExtensionRegistry().getExtensionPoint("testF1.xptF1").getExtensions()[0].getUniqueIdentifier(), "testF1.ext1");

		//Test the namespace
		assertEquals(Arrays.asList(Platform.getExtensionRegistry().getNamespaces()).contains("testF1"), true);
		assertEquals(Arrays.asList(Platform.getExtensionRegistry().getNamespaces()).contains("testF2"), false);
	}
	
	public void testG() throws IOException, BundleException {
		//fragment contributing an extension point to a plugin that do not have extension or extension point
		Bundle bundle01 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testG/1");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle01});
		Bundle bundle02 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testG/2");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle02});
	

		assertNotNull(Platform.getExtensionRegistry().getExtensionPoint("testG1.xptG2" ));
		assertEquals(Platform.getExtensionRegistry().getExtensionPoint("testG1.xptG2").getLabel(), "Label xptG2");
		assertEquals(Platform.getExtensionRegistry().getExtensionPoint("testG1.xptG2").getNamespace(), "testG1");
		assertEquals(Platform.getExtensionRegistry().getExtensionPoint("testG1.xptG2").getSchemaReference(), "schema/xptG2.exsd");
		
		//Test the namespace
		assertEquals(Arrays.asList(Platform.getExtensionRegistry().getNamespaces()).contains("testG1"), true);
		assertEquals(Arrays.asList(Platform.getExtensionRegistry().getNamespaces()).contains("testG2"), false);
	}
	
	public void testH() throws IOException, BundleException {
		//		fragment contributing an extension to a plugin that does not have extension or extension point
		Bundle bundle01 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testH/1");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle01});
		Bundle bundle02 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testH/2");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle02});
		Bundle bundle03 = BundleTestingHelper.installBundle(RuntimeTestsPlugin.getContext(), RuntimeTestsPlugin.TEST_FILES_ROOT + "registry/testH/3");
		BundleTestingHelper.refreshPackages(RuntimeTestsPlugin.getContext(), new Bundle[] {bundle03});
	
		testExtensionPoint("H1");
		
		//Test the configurataion elements
		assertEquals(Platform.getExtensionRegistry().getExtension("testH1", "xptH1", "testH3.ext1").getConfigurationElements().length, 0);
		assertEquals(Platform.getExtensionRegistry().getConfigurationElementsFor("testH1.xptH1").length, 0);
		
		//Test the number of extension in the namespace
		assertEquals(Platform.getExtensionRegistry().getExtensions("testH3").length, 1);
		
		//Test the extension
		assertNotNull(Platform.getExtensionRegistry().getExtension("testH1", "xptH1", "testH3.ext1"));
		assertNotNull(Platform.getExtensionRegistry().getExtension("testH1.xptH1", "testH3.ext1"));
		assertNotNull(Platform.getExtensionRegistry().getExtension("testH3.ext1"));  //This test exhibits a bug in the 3.0 implementation
		
		assertNotNull(Platform.getExtensionRegistry().getExtensionPoint("testH1.xptH1").getExtension("testH3.ext1"));
		assertEquals(Platform.getExtensionRegistry().getExtensionPoint("testH1.xptH1").getExtensions()[0].getUniqueIdentifier(), "testH3.ext1");
		
		//Test the namespace
		assertEquals(Arrays.asList(Platform.getExtensionRegistry().getNamespaces()).contains("testH1"), true);
		assertEquals(Arrays.asList(Platform.getExtensionRegistry().getNamespaces()).contains("testH3"), true);
		assertEquals(Arrays.asList(Platform.getExtensionRegistry().getNamespaces()).contains("testH2"), false); //fragments do not come with their namespace
	}
	
	//Test the configuration elements
	//Test the configuration elements third level
	
	//Test the cache readAll
	
	//Test stale handles, 
	//test createexecutable extension from a stale ce
	//test various methods on a delta object
	//test that configuration elements are removed 
	
	//Test that all the objects are removed.
	
	//Test the delta with more details
	
//	public static Test suite() {
//		TestSuite sameSession = new SessionTestSuite("org.eclipse.core.tests.runtime", ExtensionRegistryStaticTest.class);
//		return sameSession;
//	}
}