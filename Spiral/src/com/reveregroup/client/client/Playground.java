package com.reveregroup.client.client;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class Playground {
	boolean timerOn;
	double velocity;
	Timer timer = new CTimer();
	Carousel carousel;
	
	double acceleration = .9;
	double velocityThreshold = .01;
	
	TextBox textbox;
	Label label;
	
	public Playground(Carousel target) {
		this.carousel = target;
		
		textbox = new TextBox();
		textbox.addChangeHandler(new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				updateLabel();
			}
		});
		RootPanel.get().add(textbox);
		label = new Label();
		RootPanel.get().add(label);
		
		RootPanel.get().add(new Button("Set Velocity", new ClickHandler() {
			public void onClick(ClickEvent event) {
				ticks = 0;
				setVelocity(Double.parseDouble(textbox.getValue()));
			}
		}));
		RootPanel.get().add(new Button("Set Distance", new ClickHandler() {
			public void onClick(ClickEvent event) {
				ticks = 0;
				setVelocity(Utils.velocityForDistance(Double.parseDouble(textbox.getValue()), acceleration, velocityThreshold));
			}
		}));
	}
	
	
	private int ticks;
	private void updateLabel() {
		StringBuilder sb = new StringBuilder("Ticks: ");
		sb.append(ticks);
		try {
			double value = Double.parseDouble(textbox.getValue());
			sb.append(" d -> v = ");
			sb.append(Utils.velocityForDistance(value, acceleration, velocityThreshold));
			sb.append(" v -> t = ");
			sb.append(Utils.ticksFromStartingVelocity(value, acceleration, velocityThreshold));
			sb.append(" v -> d = ");
			sb.append(Utils.distanceFromStartingVelocity(value, acceleration, velocityThreshold));
		} catch (Exception ex) {
		}
		label.setText(sb.toString());
	}
	
	private class CTimer extends Timer {
		public void run() {
			ticks++;
			updateLabel();
			carousel.rotateBy(Utils.distanceForOneTick(velocity, acceleration));
			setVelocity(velocity * acceleration);
		}
	}
	
	public void setVelocity(double velocity) {
		this.velocity = velocity;
		if (velocity  > -velocityThreshold && velocity < velocityThreshold) {
			if (timerOn){
				timer.cancel(); 
				timerOn = false;
			}
			this.velocity = 0;
		} else if (!timerOn) {		
			timer.scheduleRepeating(33);
			timerOn = true;
			timer.run();
		}
	}
}
