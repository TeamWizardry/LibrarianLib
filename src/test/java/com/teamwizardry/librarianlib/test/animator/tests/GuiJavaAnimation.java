package com.teamwizardry.librarianlib.test.animator.tests;

import com.teamwizardry.librarianlib.features.animator.Animator;
import com.teamwizardry.librarianlib.features.animator.animations.BasicAnimation;
import com.teamwizardry.librarianlib.features.gui.GuiBase;
import com.teamwizardry.librarianlib.features.gui.components.ComponentRect;
import com.teamwizardry.librarianlib.features.gui.components.ComponentText;

import java.awt.*;
import java.util.function.Function;

/**
 * TODO: Document file GuiJavaAnimation
 * <p>
 * Created by TheCodeWarrior
 */
public class GuiJavaAnimation extends GuiBase {
	
	public float theJavaField = 0f;
	Animator animator = new Animator();
	
	public GuiJavaAnimation() {
		super(100, 100);
		
		ComponentRect rect = new ComponentRect(0, 0, 100, 100);
		rect.setColor(Color.CYAN);
		
		ComponentText text = new ComponentText(20, 20);
		text.getText_im().set(() -> "Field = " + theJavaField);
		
		BasicAnimation<GuiJavaAnimation> anim = new BasicAnimation<>(this, "theJavaField");
		anim.setDuration(40);
		anim.setTo(100);
		animator.add(anim);
		
		this.getMainComponents().add(rect, text);
	}
	
}
