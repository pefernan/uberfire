/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.uberfire.client.workbench.panels.impl;

import com.google.gwtmockito.GwtMockitoTestRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.uberfire.client.mvp.PlaceManager;
import org.uberfire.client.workbench.PanelManager;
import org.uberfire.client.workbench.part.WorkbenchPartPresenter;
import org.uberfire.client.workbench.widgets.panel.StaticFocusedResizePanel;
import org.uberfire.mvp.impl.DefaultPlaceRequest;
import org.uberfire.workbench.model.PartDefinition;
import org.uberfire.workbench.model.impl.PartDefinitionImpl;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith( GwtMockitoTestRunner.class )
public class StaticWorkbenchPanelViewTest {

    @InjectMocks
    private StaticWorkbenchPanelView view;

    // Not a @Mock or @GwtMock because we want to test the view.init() method
    private final StaticWorkbenchPanelPresenter presenter = mock( StaticWorkbenchPanelPresenter.class );

    @Mock
    private PanelManager panelManager;

    @Mock
    private PlaceManager placeManager;

    @Mock( answer = Answers.RETURNS_MOCKS )
    private StaticFocusedResizePanel panel;

    @Before
    public void setup() {
        view.postConstruct();
        view.init( presenter );
    }

    @Test
    public void addPresenterOnInit() {
        assertEquals( presenter, view.getPresenter() );
    }

    @Test
    public void addPartToPanelWhenPartViewIsNull() {
        WorkbenchPartPresenter.View viewWbPartPresenter = mock( WorkbenchPartPresenter.View.class );
        when( panel.getPartView() ).thenReturn( null );

        view.addPart( viewWbPartPresenter );

        verify( panel ).setPart( viewWbPartPresenter );

    }

    @Test
    public void removeContainedPart() {
        WorkbenchPartPresenter mockPresenter = mock( WorkbenchPartPresenter.class );
        WorkbenchPartPresenter.View mockPartView = mock( WorkbenchPartPresenter.View.class );
        PartDefinition mockPartDefinition = new PartDefinitionImpl( new DefaultPlaceRequest( "mockPlace" ) );

        when( mockPartView.getPresenter() ).thenReturn( mockPresenter );
        when( mockPresenter.getDefinition() ).thenReturn( mockPartDefinition );

        when( view.panel.getPartView() ).thenReturn( null );
        view.addPart( mockPartView );
        when( view.panel.getPartView() ).thenReturn( mockPartView );

        boolean removed = view.removePart( mockPartDefinition );

        assertTrue( removed );
        verify( panel ).clear();
    }

    @Test
    public void removeNonContainedPart() {
        WorkbenchPartPresenter mockPresenter = mock( WorkbenchPartPresenter.class );
        WorkbenchPartPresenter.View mockPartView = mock( WorkbenchPartPresenter.View.class );
        PartDefinition mockPartDefinition = new PartDefinitionImpl( new DefaultPlaceRequest( "mock1" ) );

        when( mockPartView.getPresenter() ).thenReturn( mockPresenter );
        when( mockPresenter.getDefinition() ).thenReturn( mockPartDefinition );

        WorkbenchPartPresenter mockPresenter2 = mock( WorkbenchPartPresenter.class );
        WorkbenchPartPresenter.View mockPartView2 = mock( WorkbenchPartPresenter.View.class );
        PartDefinition mockPartDefinition2 = new PartDefinitionImpl( new DefaultPlaceRequest( "mock2" ) );

        when( mockPartView2.getPresenter() ).thenReturn( mockPresenter2 );
        when( mockPresenter2.getDefinition() ).thenReturn( mockPartDefinition2 );

        when( view.panel.getPartView() ).thenReturn( null );
        view.addPart( mockPartView );
        when( view.panel.getPartView() ).thenReturn( mockPartView );

        boolean removed = view.removePart( mockPartDefinition2 );

        assertFalse( removed );
        verify( panel, never() ).clear();
    }

    @Test
    public void onResize() {
        final int width = 42;
        final int height = 10;

        view.setPixelSize( width, height );

        view.onResize();

        verify( panel ).onResize();
    }


    @Test
    public void getPartsShouldReturnCurrentPart() {
        assertFalse( view.getParts().isEmpty() );
    }
}
