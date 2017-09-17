package com.fizzikgames.roguelike.gui;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.asset.AssetLoader;

/**
 * The main menu user interface
 * @author Maxim Tiourin
 * @version 1.00
 */
public class MainMenuGUI extends GUI {
	private GUI_Element_Button btnStartGame;
	private GUI_Element_Button btnLoadGame;
	private GUI_Element_Button btnControls;
	private GUI_Element_Button btnQuitGame;
	
	public MainMenuGUI() {
		super();
		
		init();
	}
	
	@Override
	protected void init() {
		int wwidth = GameLogic.WINDOW_WIDTH;
		int wheight = GameLogic.WINDOW_HEIGHT;
		
		btnStartGame = null;
		btnLoadGame = null;
		btnControls = null;
		btnQuitGame = null;
		
		//helper vars
		GUI_Element element = null;
		
		//Setup Container
		container = new GUI_Container("mainmenu", 0, 0, wwidth, wheight);
		
		/* create UI */
		//Background Image
		element = new GUI_Element_Image(container, "image_background",
				AssetLoader.image("ui_mainmenu_image_background"), null, 0, 0, wwidth, wheight,
				GUI_Element.RENDER_IMAGE, true, Anchor.TOPLEFT);
		container.addElement(element);
		//StartGame Btn
		btnStartGame = new GUI_Element_Button(container,
				"btn_newgame",
				AssetLoader.image("ui_mainmenu_btn_image_bg_newgame"),
				AssetLoader.image("ui_gui_btn_image_highlight_01"), null, null,
				null, false, GUI_Element_Button.ClickType.LEFT, 50, 100, 300, 100, GUI_Element.RENDER_BUTTON,
				true, Anchor.TOPLEFT);
		container.addElement(btnStartGame);
		//LoadGame Btn
		/*btnLoadGame = new GUI_Element_Button(container,
				"btn_loadgame",
				AssetLoader.image("ui_mainmenu_btn_image_bg_loadgame"),
				AssetLoader.image("ui_gui_btn_image_highlight_01"), null, null,
				null, false, GUI_Element_Button.ClickType.LEFT, 50, 225, 300, 100, GUI_Element.RENDER_BUTTON,
				true, Anchor.TOPLEFT);
		container.addElement(btnLoadGame);*/
		//Controls Btn
		/*btnControls = new GUI_Element_Button(container,
				"btn_controls",
				AssetLoader.image("ui_mainmenu_btn_image_bg_controls"),
				AssetLoader.image("ui_gui_btn_image_highlight_01"), null, null,
				null, false, GUI_Element_Button.ClickType.LEFT, 50, 350, 300, 100, GUI_Element.RENDER_BUTTON,
				true, Anchor.TOPLEFT);
		container.addElement(btnControls);*/
		//QuitGame Btn
		btnQuitGame = new GUI_Element_Button(container,
				"btn_quitgame",
				AssetLoader.image("ui_mainmenu_btn_image_bg_quitgame"),
				AssetLoader.image("ui_gui_btn_image_highlight_01"), null, null,
				null, false, GUI_Element_Button.ClickType.LEFT, 50, 475, 300, 100, GUI_Element.RENDER_BUTTON,
				true, Anchor.TOPLEFT);
		container.addElement(btnQuitGame);
	}
	
	public GUI_Element_Button getButtonStartGame() {
		return btnStartGame;
	}
	
	public GUI_Element_Button getButtonLoadGame() {
		return btnLoadGame;
	}
	
	public GUI_Element_Button getButtonControls() {
		return btnControls;
	}
	
	public GUI_Element_Button getButtonQuitGame() {
		return btnQuitGame;
	}
}
