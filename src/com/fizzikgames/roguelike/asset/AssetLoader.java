package com.fizzikgames.roguelike.asset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.opengl.shader.ShaderProgram;
import org.newdawn.slick.util.Log;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.Renderable;
/**
 * The asset loader is in charge of loading every resource that the game intends to use,
 * such as images, spritesheets, sounds, fonts, interface files, scripts, etc.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class AssetLoader implements Renderable {
	private static final AssetLoader AL = new AssetLoader();
	private static HashMap<String, Asset> assets;
	private static HashMap<String, HashMap<Float, UnicodeFont>> fonts; //A complex Hashmap of fonts so we dont load two of the same font
	private static ArrayList<DeferredResource> loadList;
	private static int totalToLoad = 0;
	private static boolean ready = false;
	private static boolean loading = false;
	private static boolean loaded = false;
	private static String description = "";
	
	public AssetLoader() {
		assets = new HashMap<String, Asset>();
		fonts = new HashMap<String, HashMap<Float, UnicodeFont>>();
		loadList = new ArrayList<DeferredResource>();
	}
	
	public static void load() {
		ArrayList<Asset> assetList=  new ArrayList<Asset>();
		
		// UI
		assetList.add(new AssetImage("ui_gui_btn_01", "assets/images/ui/gui/button01.png"));
		
		assetList.add(new AssetImage("ui_gui_btn_image_highlight_01", "assets/images/ui/gui/buttonhighlight.png"));
		assetList.add(new AssetImage("ui_mainmenu_image_background", "assets/images/ui/mainmenu/background.png"));
		assetList.add(new AssetImage("ui_mainmenu_btn_image_bg_newgame", "assets/images/ui/mainmenu/btn_newgame.png"));
		assetList.add(new AssetImage("ui_mainmenu_btn_image_bg_loadgame", "assets/images/ui/mainmenu/btn_loadgame.png"));
		assetList.add(new AssetImage("ui_mainmenu_btn_image_bg_controls", "assets/images/ui/mainmenu/btn_controls.png"));
		assetList.add(new AssetImage("ui_mainmenu_btn_image_bg_quitgame", "assets/images/ui/mainmenu/btn_quitgame.png"));
			//Frame
		assetList.add(new AssetImage("ui_frame_background01", "assets/images/ui/gui/frame_background01.png"));
		assetList.add(new AssetImage("ui_frame_background02", "assets/images/ui/gui/frame_background02.png"));
		assetList.add(new AssetImage("ui_frame_background03", "assets/images/ui/gui/frame_background03.png"));
		assetList.add(new AssetImage("ui_frame_background04", "assets/images/ui/gui/frame_background04.png"));
		assetList.add(new AssetImage("ui_frame_background04_taper", "assets/images/ui/gui/frame_background04_taper.png"));
		assetList.add(new AssetImage("ui_frame_background05", "assets/images/ui/gui/frame_background05.png"));
		assetList.add(new AssetImage("ui_frame_background06", "assets/images/ui/gui/frame_background06.png"));
			//Dialog
		assetList.add(new AssetImage("ui_dialog01_background", "assets/images/ui/gui/dialog01_background.png"));
		assetList.add(new AssetImage("ui_dialog01_header_background", "assets/images/ui/gui/dialog01_header_background.png"));
		assetList.add(new AssetImage("ui_dialog01_footer_background", "assets/images/ui/gui/dialog01_footer_background.png"));
		assetList.add(new AssetImage("ui_dialog01_left_background", "assets/images/ui/gui/dialog01_left_background.png"));
		assetList.add(new AssetImage("ui_dialog01_right_background", "assets/images/ui/gui/dialog01_right_background.png"));
		assetList.add(new AssetImage("ui_dialog01_taper", "assets/images/ui/gui/dialog01_taper.png"));
			//Inventory
		assetList.add(new AssetImage("ui_inv_background", "assets/images/ui/gui/inv_background.png"));
		assetList.add(new AssetImage("ui_inv_slot_background", "assets/images/ui/gui/inv_slot_background.png"));
		assetList.add(new AssetImage("ui_inv_slot_border", "assets/images/ui/gui/inv_slot_border.png"));
			//Equipment
		assetList.add(new AssetImage("ui_equip_background", "assets/images/ui/gui/equip_background.png"));
		assetList.add(new AssetImage("ui_equip_slot_background", "assets/images/ui/gui/equip_slot_background.png"));
			//Tab
		assetList.add(new AssetImage("ui_tab01_equipment_focus", "assets/images/ui/gui/tab01_equipment_focus.png"));
		assetList.add(new AssetImage("ui_tab01_equipment_unfocus", "assets/images/ui/gui/tab01_equipment_unfocus.png"));
		assetList.add(new AssetImage("ui_tab01_stats_focus", "assets/images/ui/gui/tab01_stats_focus.png"));
		assetList.add(new AssetImage("ui_tab01_stats_unfocus", "assets/images/ui/gui/tab01_stats_unfocus.png"));
			//Scrollbar
		assetList.add(new AssetImage("ui_scrollbar_background", "assets/images/ui/gui/scrollbar_background.png"));
		assetList.add(new AssetImage("ui_scrollbar_foreground", "assets/images/ui/gui/scrollbar_foreground.png"));
		assetList.add(new AssetImage("ui_scrollbar2_background", "assets/images/ui/gui/scrollbar2_background.png"));
		assetList.add(new AssetImage("ui_scrollbar2_foreground", "assets/images/ui/gui/scrollbar2_foreground.png"));
			//Slot
		assetList.add(new AssetImage("ui_slot_background", "assets/images/ui/gui/slot_background.png"));
		assetList.add(new AssetImage("ui_slot_border", "assets/images/ui/gui/slot_border.png"));
		assetList.add(new AssetImage("ui_slot_goldborder", "assets/images/ui/gui/slot_goldborder.png"));
		assetList.add(new AssetImage("ui_slot_highlight", "assets/images/ui/gui/slot_highlight.png"));
		assetList.add(new AssetImage("ui_slot_select", "assets/images/ui/gui/slot_select.png"));
			//Button
		assetList.add(new AssetImage("ui_button_normal_background", "assets/images/ui/gui/button_normal_background.png"));
		assetList.add(new AssetImage("ui_button_normal_highlight", "assets/images/ui/gui/button_normal_highlight.png"));
		assetList.add(new AssetImage("ui_button_close_normal", "assets/images/ui/gui/button_close_normal.png"));
		assetList.add(new AssetImage("ui_button_close_highlight", "assets/images/ui/gui/button_close_highlight.png"));
		assetList.add(new AssetImage("ui_button_close_select", "assets/images/ui/gui/button_close_select.png"));
		assetList.add(new AssetImage("ui_button_arrowup_normal", "assets/images/ui/gui/button_arrowup_normal.png"));
		assetList.add(new AssetImage("ui_button_arrowup_highlight", "assets/images/ui/gui/button_arrowup_highlight.png"));
		assetList.add(new AssetImage("ui_button_arrowup_select", "assets/images/ui/gui/button_arrowup_select.png"));
			//Tooltip
		assetList.add(new AssetImage("ui_tooltip_cooldowntimer", "assets/images/ui/gui/tooltip_cooldowntimer.png"));
		assetList.add(new AssetImage("ui_tooltip_horizontalseperator", "assets/images/ui/gui/tooltip_horizontalseperator.png"));
			//Bars
		assetList.add(new AssetImage("ui_bar_health_background", "assets/images/ui/gui/health_background.png"));
		assetList.add(new AssetImage("ui_bar_health_foreground", "assets/images/ui/gui/health_foreground.png"));
		assetList.add(new AssetImage("ui_bar_mana_background", "assets/images/ui/gui/mana_background.png"));
		assetList.add(new AssetImage("ui_bar_mana_foreground", "assets/images/ui/gui/mana_foreground.png"));
		assetList.add(new AssetImage("ui_bar_xp_background", "assets/images/ui/gui/xp_background.png"));
		assetList.add(new AssetImage("ui_bar_xp_foreground", "assets/images/ui/gui/xp_foreground.png"));
		assetList.add(new AssetImage("ui_bar_xp_overlay", "assets/images/ui/gui/xp_overlay.png"));
		assetList.add(new AssetImage("ui_bar_xp_cursor", "assets/images/ui/gui/xp_cursor.png"));
			//Flair
		assetList.add(new AssetImage("ui_flair_medusafull", "assets/images/ui/gui/flair_medusafull.png"));
		assetList.add(new AssetImage("ui_flair_medusafullglow", "assets/images/ui/gui/flair_medusafullglow.png"));
		assetList.add(new AssetImage("ui_flair_medusafulloutline", "assets/images/ui/gui/flair_medusafulloutline.png"));
		assetList.add(new AssetImage("ui_flair_medusafullarrow", "assets/images/ui/gui/flair_medusafullarrow.png"));
		assetList.add(new AssetImage("ui_flair_carvedstonepanel", "assets/images/ui/gui/flair_carvedstonepanel.png"));
			//Icon
		assetList.add(new AssetImage("ui_icon_inventory", "assets/images/ui/gui/icon_inventory.png"));
		assetList.add(new AssetImage("ui_icon_charactersheet", "assets/images/ui/gui/icon_charactersheet.png"));
		assetList.add(new AssetImage("ui_icon_abilitiespage", "assets/images/ui/gui/icon_abilitiespage.png"));
		assetList.add(new AssetImage("ui_icon_dungeonmap", "assets/images/ui/gui/icon_dungeonmap.png"));
		assetList.add(new AssetImage("ui_icon_optionsmenu", "assets/images/ui/gui/icon_optionsmenu.png"));
		assetList.add(new AssetImage("ui_icon_equipment_head", "assets/images/ui/gui/icon_charactersheet.png"));
		assetList.add(new AssetImage("ui_icon_equipment_chest", "assets/images/ui/gui/icon_charactersheet.png"));
		assetList.add(new AssetImage("ui_icon_equipment_legs", "assets/images/ui/gui/icon_charactersheet.png"));
		assetList.add(new AssetImage("ui_icon_equipment_hands", "assets/images/ui/gui/icon_charactersheet.png"));
		assetList.add(new AssetImage("ui_icon_equipment_feet", "assets/images/ui/gui/icon_charactersheet.png"));
		assetList.add(new AssetImage("ui_icon_equipment_mainhand", "assets/images/ui/gui/icon_charactersheet.png"));
		assetList.add(new AssetImage("ui_icon_equipment_offhand", "assets/images/ui/gui/icon_charactersheet.png"));
		
		// Load Tilesets
		assetList.add(new AssetSpriteSheet("tileset_001", "assets/images/tileset/tileset001.png"));
		assetList.add(new AssetSpriteSheet("tileset_extras", "assets/images/tileset/tilesetextras001.png"));
		
		// Load Characters
		assetList.add(new AssetSpriteSheet("character_player_001", "assets/images/character/player001.png"));
		assetList.add(new AssetSpriteSheet("character_monster_001", "assets/images/character/monster001.png"));
		assetList.add(new AssetSpriteSheet("character_monster_goblin01", "assets/images/character/goblin01.png"));
		assetList.add(new AssetSpriteSheet("character_monster_goblin02", "assets/images/character/goblin02.png"));
		assetList.add(new AssetSpriteSheet("character_monster_spider01", "assets/images/character/spider01.png"));
		assetList.add(new AssetSpriteSheet("character_monster_skeleton01", "assets/images/character/skeleton01.png"));
		assetList.add(new AssetSpriteSheet("character_monster_golem01", "assets/images/character/golem01.png"));
		
		// Load Extras
		assetList.add(new AssetSpriteSheet("extra_targetcursor", "assets/images/extra/targetcursor.png"));
		assetList.add(new AssetSpriteSheet("extra_trap", "assets/images/extra/trap.png"));
		assetList.add(new AssetSpriteSheet("extra_shrine", "assets/images/extra/shrine.png"));
		assetList.add(new AssetSpriteSheet("extra_shrine02", "assets/images/extra/shrine02.png"));
		assetList.add(new AssetSpriteSheet("extra_item", "assets/images/extra/item.png"));
		assetList.add(new AssetSpriteSheet("extra_chest", "assets/images/extra/chest.png"));
		
		// Load Abilities
		assetList.add(new AssetImage("ability_fireball", "assets/images/ability/fireball.png"));
		assetList.add(new AssetImage("ability_hemorrhage", "assets/images/ability/hemorrhage.png"));
		assetList.add(new AssetImage("ability_allsight", "assets/images/ability/allsight.png"));
		assetList.add(new AssetImage("ability_arcaneexplosion", "assets/images/ability/arcaneexplosion.png"));
		assetList.add(new AssetImage("ability_teleport", "assets/images/ability/teleport.png"));
		assetList.add(new AssetImage("ability_blink", "assets/images/ability/blink.png"));
		assetList.add(new AssetImage("ability_overwhelmingpower", "assets/images/ability/overwhelmingpower.png"));
			//Ability Icon Effect
		assetList.add(new AssetImage("ability_effect_oom", "assets/images/ability/effect_oom.png"));
		assetList.add(new AssetImage("ability_effect_cd", "assets/images/ability/effect_cd.png"));
		
		// Load Items
		assetList.add(new AssetImage("item_key", "assets/images/item/key.png"));
		assetList.add(new AssetImage("item_healthelixir", "assets/images/item/healthelixir.png"));
		assetList.add(new AssetImage("item_manaelixir", "assets/images/item/manaelixir.png"));
		assetList.add(new AssetImage("item_bow", "assets/images/item/bow.png"));
		assetList.add(new AssetImage("item_battleaxe", "assets/images/item/battleaxe.png"));
		assetList.add(new AssetImage("item_dagger", "assets/images/item/dagger.png"));
		assetList.add(new AssetImage("item_headpiece", "assets/images/item/headpiece.png"));
		assetList.add(new AssetImage("item_chestpiece", "assets/images/item/chestpiece.png"));
		assetList.add(new AssetImage("item_legpiece", "assets/images/item/legpiece.png"));
		assetList.add(new AssetImage("item_footpiece", "assets/images/item/footpiece.png"));
		assetList.add(new AssetImage("item_handpiece", "assets/images/item/handpiece.png"));
		
		// Load Fonts
		assetList.add(new AssetFont("font_nightserif", "assets/fonts/NightSerif.ttf"));
		assetList.add(new AssetFont("font_droidserif", "assets/fonts/DroidSerif.ttf"));
		assetList.add(new AssetFont("font_generisserif", "assets/fonts/GenerisSerif.ttf"));
		assetList.add(new AssetFont("font_coolserif", "assets/fonts/CoolSerif.ttf"));
		assetList.add(new AssetFont("font_codaregular", "assets/fonts/CodaRegular.ttf"));
		assetList.add(new AssetFont("font_receipt", "assets/fonts/Receipt.ttf"));
		assetList.add(new AssetFont("font_expressway", "assets/fonts/Expressway.ttf"));
		
		//Load Shaders
		//assetList.add(new AssetShader("shader_lightradius", "assets/shaders/vert/pass.vert", "assets/shaders/frag/test.frag"));
					
		/* Convert Asset List to Hash Map using asset ids as keys*/
		for (Asset e : assetList) {
			assets.put(e.getId(), e);
		}
					
		totalToLoad = loadList.size();

		ready = true;
	}
	
	public static void update(GameContainer gc) {
		if (ready) {			
			if (loadList.size() > 0) {
				loading = true;
				DeferredResource next = loadList.remove(0);
				description = next.getDescription();
				//System.out.println("Loading Resource out of " + totalToLoad + "! " + description);
				try {
					next.load();
				} catch (IOException e) {
					Log.error("Unable to defer load Asset: " + description, e);
				}
			}
			else {
				loading = false;
				loaded = true;
			}
		}
	}
	
	public void render(GameContainer gc, Graphics g) {
		if (ready) {
			if (loading) {
				//Draw Background
				g.setColor(Color.black);
				g.fillRect(0, 0, GameLogic.WINDOW_WIDTH, GameLogic.WINDOW_HEIGHT);
				
				//Draw Loading info
				int total = totalToLoad;
				int left = total - loadList.size();
				int percent = (int) (((double) left / (double) total) * 100.00);
				
				g.setColor(Color.white);
				g.drawString("Loading: " + description, 25, 25);
				g.drawString(left + "/" + total + " (" + percent + "%)", 25, 40);
			}
		}
	}
	
	public static AssetLoader get() {
		return AL;
	}
	
	public static ArrayList<DeferredResource> getLoadingList() {
		return loadList;
	}
	
	public static Image image(String id) {
		return ((AssetImage) assets.get(id)).getImage();
	}
	
	public static SpriteSheet spritesheet(String id) {
		return ((AssetSpriteSheet) assets.get(id)).getSpriteSheet();
	}
	
	/**
	 * Once a font at a specific size has its glyphs loaded, it will no longer need to be twice.
	 */
	public static UnicodeFont font(String id, float size) {
		if (fonts.get(id) == null) {
			fonts.put(id, new HashMap<Float, UnicodeFont>());
		}
		if (fonts.get(id).get(size) == null) {
			UnicodeFont font = ((AssetFont) assets.get(id)).getFont(size);			
			fonts.get(id).put(size, font);
			return font;
		}
		
		return fonts.get(id).get(size);
	}
	
	public static Sound sound(String id) {
		return ((AssetSound) assets.get(id)).getSound();
	}
	
	public static ShaderProgram shader(String id) {
		return ((AssetShader) assets.get(id)).getShader();
	}
	
	public static boolean isReady() {
		return ready;
	}
	
	public static boolean isLoaded() {
		return loaded;
	}
}
