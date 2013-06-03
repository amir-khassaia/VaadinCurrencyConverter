/*
 * Copyright 2009 IT Mill Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.bitpimp.VaadinCurrencyConverter;

import java.util.Date;

import com.google.common.base.Throwables;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.server.DefaultErrorHandler;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

/**
 * Sample application to obtain currency exchange rates from yahoo finance.
 * 
 * The Application's "main" class
 */
@SuppressWarnings("serial")
public class MyVaadinApplication extends UI
{
	private static final String CURRENCY_CODE_REQUIRED = "Valid 3 character currency symbol is required";
	private final YQLCurrencyConverter converter = new YQLCurrencyConverter();
	
	@Override
	protected void init(VaadinRequest request) {
        // Set the window or tab title
        getPage().setTitle("Yahoo Currency Converter");

        // Create the content root layout for the UI
        final FormLayout content = new FormLayout();
        content.setMargin(true);
        final Panel panel = new Panel(content);
        panel.setWidth("500");
        panel.setHeight("400");
        final VerticalLayout root = new VerticalLayout();
        root.addComponent(panel);
        root.setComponentAlignment(panel, Alignment.MIDDLE_CENTER);
        root.setSizeFull();
        root.setMargin(true);
        
        setContent(root);
        
        content.addComponent(new Embedded("", new ExternalResource(
                "https://vaadin.com/vaadin-theme/images/vaadin/vaadin-logo-color.png")));
        content.addComponent(new Embedded("", new ExternalResource(
                "http://images.wikia.com/logopedia/images/e/e4/YahooFinanceLogo.png")));
        
        // Display the greeting
        final Label heading = new Label(
        		"<b>Simple Currency Converter " +
        		"Using YQL/Yahoo Finance Service</b>", 
        		ContentMode.HTML);
        heading.setWidth(null);
		content.addComponent(heading);
        
		// Build the set of fields for the converter form
        final TextField fromField = new TextField("From Currency", "AUD");
        fromField.setRequired(true);
        fromField.addValidator(new StringLengthValidator(CURRENCY_CODE_REQUIRED, 3, 3, false));
		content.addComponent(fromField);
		
        final TextField toField = new TextField("To Currency", "USD");
        toField.setRequired(true);
        toField.addValidator(new StringLengthValidator(CURRENCY_CODE_REQUIRED, 3, 3, false));
		content.addComponent(toField);
		
		final TextField resultField = new TextField("Result");
		resultField.setEnabled(false);
		content.addComponent(resultField);
		
		final TextField timeField = new TextField("Time");
		timeField.setEnabled(false);
		content.addComponent(timeField);

        final Button submitButton = new Button("Submit", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// Do the conversion
				final String result = converter.convert(fromField.getValue().toUpperCase(), 
						toField.getValue().toUpperCase());
				if (result != null) {
					resultField.setValue(result);
					timeField.setValue(new Date().toString());
				}
			}
		});
		content.addComponent(submitButton);
		
		// Configure the error handler for the UI
		UI.getCurrent().setErrorHandler(new DefaultErrorHandler() {
		    @Override
		    public void error(com.vaadin.server.ErrorEvent event) {
		        // Find the final cause
		        String cause = "<b>The operation failed :</b><br/>";
		        Throwable th = Throwables.getRootCause(event.getThrowable());
		        if (th != null)
		        	cause += th.getClass().getName() + "<br/>";
		        
		        // Display the error message in a custom fashion
		        content.addComponent(new Label(cause, ContentMode.HTML));
		           
		        // Do the default error handling (optional)
		        doDefault(event);
		    } 
		});
	}
    
}
