package com.fizzikgames.roguelike.gui;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Rectangle;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.TextField;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.util.StringUtil;

/**
 * A GUI Textbox is a element that consumes key events to enter text from the user
 * @author Maxim Tiourin
 * @version 1.00
 */
public class GUI_Element_TextBox extends GUI_Element implements ComponentListener, KeyFocus {
	protected TextField textbox;
	protected String value;
	protected UnicodeFont font;
	protected Color textColor;
	protected Color bgColor;
	protected Color borderColor;
	protected int width;
	protected int height;
	protected int maxLength;
	protected boolean masked;
	private String maskValue;
	private String submitValue;
	protected GUI_Element_Button enterButton;
	
	public GUI_Element_TextBox(GUI_Container container, String id, String value, UnicodeFont font, int maxLength, 
			Color textColor, Color bgColor, Color borderColor, Tooltip tooltip, float x, float y, int width, 
			int height, int renderPriority, boolean masked, boolean visible, Anchor anchor) {
		super(container, id, x, y, renderPriority, visible, anchor);		
		this.font = font;
		this.maxLength = maxLength;
		this.textColor = textColor;
		this.bgColor = bgColor;
		this.borderColor = borderColor;
		this.tooltip = tooltip;
		this.width = width;
		this.height = height;
		this.enterButton = null;
		this.masked = masked;
		this.maskValue = "";
		this.submitValue = "";
		
		textbox = new TextField(GameLogic.CONTAINER, font, (int) x(), (int) y(), getWidth(), getHeight(), this);
		setValue(value);
		textbox.setLocation((int) x(), (int) y());
		textbox.setTextColor(this.textColor);
		textbox.setBackgroundColor(this.bgColor);
		textbox.setBorderColor(this.borderColor);
		textbox.setConsumeEvents(true);
		textbox.setMaxLength(maxLength);
		textbox.setCursorVisible(true);
		textbox.setAcceptingInput(true);
		textbox.setCursorPos(0);
	}
	
	@Override
	public void update(GameContainer gc, GUI_Element caller) {
		if (visible()) {
			textbox.setLocation((int) x(), (int) y());
			
			value = textbox.getText();
			
			//Update mask
			if (masked) {
				setValue(maskValue());
			}
			
			if ((caller == null
					|| Rectangle.contains(container.mousex(), container.mousey(), caller.x(), caller.y(), caller.getWidth(), caller.getHeight()))
					&& Rectangle.contains(container.mousex(), container.mousey(), x(), y(), getWidth(), getHeight())) {
				setFocus(true);
			}
			else {
				setFocus(false);
			}
		}
		
		if (gc.getInput().isKeyPressed(Input.KEY_ENTER)) {
			if (textbox.hasFocus()) {
				//Reminder:
				//Reason this key polling doesn't use Controls class is because it is non-modified control
				//Also reason that i dont check focus until keys are pressed is so the event gets eaten up for this instance
				//because problems I was running into were it would still read the event after the box got focus.
				textbox.setFocus(false);
				if (enterButton != null) {
					enterButton.simulateClick();
				}
				
				//MD5 Encryption of the masked word
				if (masked) {
					submitValue = StringUtil.md5(maskValue);
					maskValue = "";
				}
			}
		}
		else if (gc.getInput().isKeyPressed(Input.KEY_ESCAPE)) {
			if (textbox.hasFocus()) {
				textbox.setFocus(false);
			}
		}
		
		if (textbox.hasFocus() && (container.getKeyFocus() != this)) {
			container.changeKeyFocus(this);
		}
		else if (!textbox.hasFocus()) {
			if (container.getKeyFocus() == this) {
				container.changeKeyFocus(null);
			}
		}
	}
	
	@Override
	public void render(Graphics g) {
		if (visible()) {
			//Label
			textbox.render(GameLogic.CONTAINER, g);
			
			if ((tooltip != null) && hasFocus()) container.addTooltipToRenderQueue(tooltip);
		}
	}
	
	public void componentActivated(AbstractComponent source) {
		//Do nothing yet
		//System.out.println("activated");
	}
	
	public Color getTextColor() {
		return textColor;
	}
	
	public void setTextColor(Color c) {
		textColor = c;
	}
	
	public UnicodeFont getFont() {
		return font;
	}
	
	public void setFont(UnicodeFont font) {
		this.font = font;
	}
	
	public String getValue() {
		return textbox.getText();
	}
	
	public void setValue(String s) {
		textbox.setText(s);
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public int getMaxLength() {
		return maxLength;
	}
	
	public void setMaxLength(int l) {
		maxLength = l;
	}
	
	public void setEnterButton(GUI_Element_Button e) {
		enterButton = e;
	}
	
	public void removeKeyFocus() {
		textbox.setFocus(false);
	}
	
	private int countMaskedChars() {
		int count = 0;
		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i) == '*') {
				count++;
			}
		}
		return count;
	}
	
	private String maskValue() {
		String str = "";
		boolean hadUnmask = false;
		boolean invalid = false;
		
		for (int i = 0; i < value.length(); i++) {
			if (value.charAt(i) != '*') {
				hadUnmask = true;
				str += String.valueOf(value.charAt(i));
			}
			else {
				if (hadUnmask) invalid = true;
			}
		}
		
		if ((countMaskedChars() < maskValue.length()) || invalid) {
			//Reset the pass since user modified
			value = "";
			maskValue = "";			
			return value;
		}
		
		maskValue += str;
		
		value = "";
		
		for (int i = 0; i < maskValue.length(); i++) {
			value += "*";
		}
		
		return value;
	}
	
	public String getSubmitValue() {
		return submitValue;
	}
}
