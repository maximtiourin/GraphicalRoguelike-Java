package com.fizzikgames.roguelike.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.geom.Vector2f;

import com.fizzikgames.roguelike.GameLogic;
import com.fizzikgames.roguelike.asset.AssetLoader;
import com.fizzikgames.roguelike.entity.GameCharacter;
import com.fizzikgames.roguelike.entity.GameCharacterEvent;
import com.fizzikgames.roguelike.entity.GameCharacterListener;
import com.fizzikgames.roguelike.entity.mechanics.ability.Ability;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Equipment;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.EquippableItem;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Inventory;
import com.fizzikgames.roguelike.entity.mechanics.ability.item.Item;
import com.fizzikgames.roguelike.entity.mechanics.buff.TemporaryStatModifier;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CurrentExperience;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CurrentHealth;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_CurrentMana;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalExperience;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalHealth;
import com.fizzikgames.roguelike.entity.mechanics.stat.Stat_TotalMana;

/**
 * Implements the entire gui used during gameplay.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class PlayerGUI extends GUI implements GameCharacterListener {
	private static final int RENDER_SUBCONTAINER = 10000;
	private HashMap<String, GUI_Container> popupContainers; //Map of popupcontainers, so new ones can be created, and old ones can be reused.
	private GameCharacter playerGameCharacter;
	private GUI_Container deathPane;
	/* Character Sheet */
	private static final Vector2f[] EQUIPMENT_OFFSETS = {
		new Vector2f(0, -105),			//Head 
		new Vector2f(0, -48), 			//Chest
		new Vector2f(0, 25), 			//Legs
		new Vector2f(-50, -32),			//Hands
		new Vector2f(-25, 100), 		//Feet
		new Vector2f(-60, 5), 			//MainHand
		new Vector2f(60, 5),			//OffHand
		};
	private static final Vector2f[] EQUIPMENT_SIZES = {
		new Vector2f(48, 48),			//Head 
		new Vector2f(64, 64), 			//Chest
		new Vector2f(64, 64), 			//Legs
		new Vector2f(32, 32),			//Hands
		new Vector2f(40, 40), 			//Feet
		new Vector2f(40, 40), 			//MainHand
		new Vector2f(40, 40),			//OffHand
		};
	private GUI_Container[] equipmentSlots;
	private GUI_Element_DragDropTarget[] equipmentDragDrops;
	private GameCharacterListener[] equipmentStatusListeners;
	private GUI_Container charSheetPane;
	private GUI_Container charSheetStatsPane;
	private GUI_Element_VerticalScrollArea charSheetVsa;
	private ArrayList<GameCharacterListener> charSheetStatListeners;
	/* Action Buttons */
	public static final int ACTIONBARPANE_HEIGHT = 90;
	private static final int ACTION_BUTTON_COUNT = 10;
	private static final int PANEL_BUTTON_COUNT = 5;
	private static final int[] ACTION_BUTTON_BINDS = {Input.KEY_1, Input.KEY_2, Input.KEY_3, Input.KEY_4, Input.KEY_5, Input.KEY_6, Input.KEY_7,
		Input.KEY_8, Input.KEY_9, Input.KEY_0};
	private static final int[] PANEL_BUTTON_BINDS = {Input.KEY_B, Input.KEY_C, Input.KEY_V, Input.KEY_TAB, Input.KEY_ESCAPE};
	private Vector2f contextTooltipPos;
	private GUI_Element_Button[] actionButtons;
	private GameCharacterListener[] actionButtonStatusListeners;
	private GUI_Element_Button[] panelButtons;
	/* Buff / Debuff */
	private static final int BUFF_ICON_COUNT = 16;
	private static final int BUFF_ICON_COLUMN_SIZE = 8;
	private static final int DEBUFF_ICON_COUNT = 16;
	private static final int DEBUFF_ICON_COLUMN_SIZE = 8;
	private GUI_Container[] buffIcons;
	private GUI_Container[] debuffIcons;
	private GUI_Element_Image[] buffIconImages;
	private GUI_Element_Image[] debuffIconImages;
	private GUI_Element_Label[] buffIconDurationLabels;
	private GUI_Element_Label[] debuffIconDurationLabels;
	private GUI_Element_Label[] buffIconStackLabels;
	private GUI_Element_Label[] debuffIconStackLabels;
	/* Inventory */
	private GUI_Container inventoryPane;
	private GUI_Container[][] inventorySlots;
	private GUI_Element_DragDropTarget[][] inventoryDragDrops;
	private GameCharacterListener[][] inventoryStatusListeners;
	private GUI_Element_Label[][] inventoryStackLabels;
	/* Abilities Page Pane */
	private GUI_Container abilitiesPane;
	private GUI_Element_VerticalScrollArea abilitiesVsa;
	private ArrayList<GameCharacterListener> abilityStatusListeners;
	/* Health/Mana Pane */
	private GUI_Element_MaskImage currentHealthImage;
	private GUI_Element_Image totalHealthImage;
	private GUI_Element_Label currentHealthLabel;
	private GUI_Element_MaskImage currentManaImage;
	private GUI_Element_Image totalManaImage;
	private GUI_Element_Label currentManaLabel;
	/* XP Pane */
	private GUI_Container xpPane;
	private GUI_Element_MaskImage xpCurrentImage;
	private GUI_Element_Image xpCursorImage;
	private GUI_Element_Label xpCurrentLabel;
	/* Levelup Pane */
	private GUI_Element_RollOverConstraint medusaRollOver;
	private GUI_Element_MaskImage medusaOutline;
	/* Dungeonmap Pane */
	private GUI_Container dungeonMapPane;
	private GUI_Element_Image dungeonMapLevelImage;
	private GUI_Container dungeonMapPane2;
    private GUI_Element_Image dungeonMapLevelImage2;
	
	public PlayerGUI(GameCharacter playerGameCharacter) {
		super();
		this.playerGameCharacter = playerGameCharacter;
		this.popupContainers = new HashMap<String, GUI_Container>();
		init();
	}
	
	public void setPlayer(GameCharacter gamechar) {
	    playerGameCharacter = gamechar;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void eventPerformed(GameCharacterEvent e) {
		final GameCharacter gamechar = e.getGameCharacter();
		
		if (e.getEventType().equals(GameCharacterEvent.Type.Initialized.getType())) {
			/* Initialized */			
			reconstructCharSheetStatsVsa(e);
		}
		else if (e.getEventType().equals(GameCharacterEvent.Type.Died.getType())) {
            /* Died */           
            deathPane.setVisible(true);
        }
		else if (e.getEventType().equals(GameCharacterEvent.Type.Ability_Added.getType())) {
			/* Ability Added */			
			reconstructAbilitiesVsa(e);
		}
		else if (e.getEventType().equals(GameCharacterEvent.Type.Level_Modified.getType())) {
			/* Check Level Changes */
			//medusaRollOver.setVisible(true);
			//medusaOutline.setVisible(true);
		}
		else if (e.getEventType().equals(GameCharacterEvent.Type.Buff_Modified.getType())) {
			/* Buff Added */
			Tooltip ttip;
			ArrayList<TemporaryStatModifier> statmods = 
					(ArrayList<TemporaryStatModifier>) (((ArrayList<TemporaryStatModifier>) gamechar.getBuffs(true)).clone());
			int count = statmods.size();
			Collections.sort(statmods, new TemporaryStatModifier.DurationComparator());
			
			//Update the necessary buffs
			for (int i = 0; i < BUFF_ICON_COUNT; i++) {				
				if (i <= (count - 1)) {
					TemporaryStatModifier statmod = statmods.get(i);
					
					buffIcons[i].setVisible(true);
					ttip = GUI_AdvancedTooltip.createBuffDebuffTooltip(statmod.getDescription());
					buffIconImages[i].setImage(statmod.getIconImage().getScaledCopy(buffIcons[i].getWidth(), buffIcons[i].getHeight()), true);
					buffIconImages[i].setTooltip(ttip);
					ttip.setParent(buffIconImages[i]);
					if (!statmod.isPermanent()) {
						buffIconDurationLabels[i].setVisible(true);
						buffIconDurationLabels[i].setLabel(statmod.getCurrentDurationLeft() + "");
					}
					else {
						buffIconDurationLabels[i].setVisible(false);
					}
					if (statmod.isStackable()) {
						buffIconStackLabels[i].setVisible(true);
						buffIconStackLabels[i].setLabel(statmod.getCurrentStacks() + "");
					}
					else {
						buffIconStackLabels[i].setVisible(false);
					}
				}
				else {
					buffIcons[i].setVisible(false);
				}
			}
		}
		else if (e.getEventType().equals(GameCharacterEvent.Type.Debuff_Modified.getType())) {
			/* Deuff Added */
			Tooltip ttip;
			ArrayList<TemporaryStatModifier> statmods = 
					(ArrayList<TemporaryStatModifier>) (((ArrayList<TemporaryStatModifier>) gamechar.getDebuffs(true)).clone());
			int count = statmods.size();
			Collections.sort(statmods, new TemporaryStatModifier.DurationComparator());
			
			//Update the necessary debuffs
			for (int i = 0; i < DEBUFF_ICON_COUNT; i++) {
				if (i <= (count - 1)) {
					TemporaryStatModifier statmod = statmods.get(i);
					
					debuffIcons[i].setVisible(true);
					ttip = GUI_AdvancedTooltip.createBuffDebuffTooltip(statmod.getDescription());
					debuffIconImages[i].setImage(statmod.getIconImage().getScaledCopy(debuffIcons[i].getWidth(), debuffIcons[i].getHeight()), true);
					debuffIconImages[i].setTooltip(ttip);
					ttip.setParent(debuffIconImages[i]);
					if (!statmod.isPermanent()) {
						debuffIconDurationLabels[i].setVisible(true);
						debuffIconDurationLabels[i].setLabel(statmod.getCurrentDurationLeft() + "");
					}
					else {
						debuffIconDurationLabels[i].setVisible(false);
					}
					if (statmod.isStackable()) {
						debuffIconStackLabels[i].setVisible(true);
						debuffIconStackLabels[i].setLabel(statmod.getCurrentStacks() + "");
					}
					else {
						debuffIconStackLabels[i].setVisible(false);
					}
				}
				else {
					debuffIcons[i].setVisible(false);
				}
			}
		}
		else if (e.getEventType().equals(GameCharacterEvent.Type.Stat_CurrentHealth_Modified.getType())) {
			/* Current Health Modified */	
			Stat chealth = gamechar.getStat(Stat_CurrentHealth.REFERENCE);
			Stat thealth = gamechar.getStat(Stat_TotalHealth.REFERENCE);
			int maskwidth = (int) Math.floor((chealth.getModifiedValue() / thealth.getModifiedValue()) * totalHealthImage.getWidth());
			currentHealthImage.setMaskWidth(maskwidth);
			currentHealthLabel.setLabel(((int) chealth.getModifiedValue()) + " / " + ((int) thealth.getModifiedValue()));
		}
		else if (e.getEventType().equals(GameCharacterEvent.Type.Stat_TotalHealth_Modified.getType())) {
			/* Total Health Modified */
			Stat chealth = gamechar.getStat(Stat_CurrentHealth.REFERENCE);
			Stat thealth = gamechar.getStat(Stat_TotalHealth.REFERENCE);
			int maskwidth = (int) Math.floor((chealth.getModifiedValue() / thealth.getModifiedValue()) * totalHealthImage.getWidth());
            currentHealthImage.setMaskWidth(maskwidth);
			currentHealthLabel.setLabel(((int) chealth.getModifiedValue()) + " / " + ((int) thealth.getModifiedValue()));
		}
		else if (e.getEventType().equals(GameCharacterEvent.Type.Stat_CurrentMana_Modified.getType())) {
			/* Current Mana Modified */			
			Stat cmana = gamechar.getStat(Stat_CurrentMana.REFERENCE);
			Stat tmana = gamechar.getStat(Stat_TotalMana.REFERENCE);
			int maskwidth = (int) Math.floor((cmana.getModifiedValue() / tmana.getModifiedValue()) * totalManaImage.getWidth());
			currentManaImage.setMaskWidth(maskwidth);
			currentManaLabel.setLabel(((int) cmana.getModifiedValue()) + " / " + ((int) tmana.getModifiedValue()));
		}
		else if (e.getEventType().equals(GameCharacterEvent.Type.Stat_TotalMana_Modified.getType())) {
			/*Total Mana Modified */
			Stat cmana = gamechar.getStat(Stat_CurrentMana.REFERENCE);
			Stat tmana = gamechar.getStat(Stat_TotalMana.REFERENCE);
			int maskwidth = (int) Math.floor((cmana.getModifiedValue() / tmana.getModifiedValue()) * totalManaImage.getWidth());
            currentManaImage.setMaskWidth(maskwidth);
			currentManaLabel.setLabel(((int) cmana.getModifiedValue()) + " / " + ((int) tmana.getModifiedValue()));
		}
		else if (e.getEventType().equals(GameCharacterEvent.Type.Stat_CurrentExperience_Modified.getType())) {
			/* Current Experience Modified */			
			Stat cxp = gamechar.getStat(Stat_CurrentExperience.REFERENCE);
			Stat txp = gamechar.getStat(Stat_TotalExperience.REFERENCE);
			int maskwidth = (int) Math.floor((cxp.getModifiedValue() / txp.getModifiedValue()) * xpPane.getWidth());
			xpCurrentImage.setMaskWidth(maskwidth);
			xpCursorImage.addX((xpPane.x() + maskwidth - 4) - xpCursorImage.x()); 
			xpCurrentLabel.setLabel(((int) cxp.getModifiedValue()) + " / " + ((int) txp.getModifiedValue()));
		}
		
		//Extra Check For Updating Dungeon Map
		if (e.getEventType().equals(GameCharacterEvent.Type.Initialized.getType())
		        || e.getEventType().equals(GameCharacterEvent.Type.Finished_Stairs.getType())
		        || e.getEventType().equals(GameCharacterEvent.Type.Position_Modified.getType())) {
		    Image image = e.getGameCharacter().getLevel().getPixelImageOfLevelState();
		    dungeonMapLevelImage.setImage(image, true);
		    dungeonMapLevelImage2.setImage(image, true);
		}
	}
	
	@Override
	public List<String> getEventTypes() {
		ArrayList<String> types = new ArrayList<String>();
		//Initialization
		types.add(GameCharacterEvent.Type.Initialized.getType());
		//Death
		types.add(GameCharacterEvent.Type.Died.getType());
		//Abilities
		types.add(GameCharacterEvent.Type.Ability_Added.getType());
		//Finished Stairs
		types.add(GameCharacterEvent.Type.Finished_Stairs.getType());
		//Position Modified
        types.add(GameCharacterEvent.Type.Position_Modified.getType());
		//Level
		types.add(GameCharacterEvent.Type.Level_Modified.getType());
		//Buffs/Debuffs
		types.add(GameCharacterEvent.Type.Buff_Modified.getType());
		types.add(GameCharacterEvent.Type.Debuff_Modified.getType());
		//Stats
		types.add(GameCharacterEvent.Type.Stat_CurrentHealth_Modified.getType());
		types.add(GameCharacterEvent.Type.Stat_TotalHealth_Modified.getType());
		types.add(GameCharacterEvent.Type.Stat_CurrentMana_Modified.getType());
		types.add(GameCharacterEvent.Type.Stat_TotalMana_Modified.getType());
		types.add(GameCharacterEvent.Type.Stat_CurrentExperience_Modified.getType());
		return types;
	}
	
	@Override
	public void keyPressed(int key, char c) {
		//Check key only if this gui doesnt currently have key focus in a textbox
		if (!this.hasKeyFocus()) {
			//Check Enter for context sensitive actions
			boolean contextEaten = false;
			if (key == Input.KEY_ENTER || key == Input.KEY_ESCAPE) {
				//PopupContainers YES button
				Set<Map.Entry<String, GUI_Container>> set = popupContainers.entrySet();
				for (Map.Entry<String, GUI_Container> e : set) {
					GUI_Container dialog = e.getValue();
					if (dialog.isVisible()) {
						if (key == Input.KEY_ENTER) {
							GUI_Element_Button button = (GUI_Element_Button) dialog.getElement("btn_yes");
							button.simulateFullClick();
						}
						else if (key == Input.KEY_ESCAPE) {
							GUI_Element_Button button = (GUI_Element_Button) dialog.getElement("btn_no");
							button.simulateFullClick();
						}
						
						contextEaten = true;
					}
				}
			}
			
			if (!contextEaten) {
				//Check Action buttons
				for (int i = 0; i < ACTION_BUTTON_COUNT; i++) {
					if (key == ACTION_BUTTON_BINDS[i]) {
						actionButtons[i].simulateFullClick();
						return;
					}
				}
				//Check Panel buttons
				for (int i = 0; i < PANEL_BUTTON_COUNT; i++) {
					if (key == PANEL_BUTTON_BINDS[i]) {
						panelButtons[i].simulateFullClick();
						return;
					}
				}
			}
		}
	}
	
	@Override 
	public void mouseWheelMoved(int change) {
		container.setMouseWheelMoved(change);
	}

	@Override
	protected void init() {
	    deathPane = null;
		/* Character Sheet */
		equipmentSlots = new GUI_Container[Equipment.INDEX_COUNT];
		equipmentDragDrops = new GUI_Element_DragDropTarget[Equipment.INDEX_COUNT];
		equipmentStatusListeners = new GameCharacterListener[Equipment.INDEX_COUNT];
		charSheetPane = null;
		charSheetStatsPane = null;
		charSheetVsa = null;
		charSheetStatListeners = new ArrayList<GameCharacterListener>();
		/* Action Buttons */
		actionButtons = new GUI_Element_Button[ACTION_BUTTON_COUNT];
		actionButtonStatusListeners = new GameCharacterListener[ACTION_BUTTON_COUNT];
		panelButtons = new GUI_Element_Button[PANEL_BUTTON_COUNT];
		/* Buff / Debuff */
		buffIcons = new GUI_Container[BUFF_ICON_COUNT];
		debuffIcons = new GUI_Container[DEBUFF_ICON_COUNT];
		buffIconImages = new GUI_Element_Image[BUFF_ICON_COUNT];
		debuffIconImages = new GUI_Element_Image[DEBUFF_ICON_COUNT];
		buffIconDurationLabels = new GUI_Element_Label[BUFF_ICON_COUNT];
		debuffIconDurationLabels = new GUI_Element_Label[DEBUFF_ICON_COUNT];
		buffIconStackLabels = new GUI_Element_Label[BUFF_ICON_COUNT];
		debuffIconStackLabels = new GUI_Element_Label[DEBUFF_ICON_COUNT];
		/* Inventory */
		final int invrows = playerGameCharacter.getInventory().getRowCount();
		final int invcolumns = playerGameCharacter.getInventory().getColumnCount();
		inventoryPane = null;
		inventorySlots = new GUI_Container[invrows][invcolumns];
		inventoryDragDrops = new GUI_Element_DragDropTarget[invrows][invcolumns];
		inventoryStatusListeners = new GameCharacterListener[invrows][invcolumns];
		inventoryStackLabels = new GUI_Element_Label[invrows][invcolumns];
		/* Abilities Page Pane */
		abilitiesPane = null;
		abilitiesVsa = null;
		abilityStatusListeners = new ArrayList<GameCharacterListener>();
		/* Health/Mana Pane */
		currentHealthImage = null;
		totalHealthImage = null;
		currentHealthLabel = null;
		currentManaImage = null;
		totalManaImage = null;
		currentManaLabel = null;
		/* XP Pane */
		xpPane = null;
		xpCurrentImage = null;
		xpCursorImage = null;
		xpCurrentLabel = null;
		/* Levelup Pane */
		medusaRollOver = null;
		medusaOutline = null;
		/* Dungeon Map Pane */
		dungeonMapPane = null;
		dungeonMapLevelImage = null;
		
		int wwidth = GameLogic.WINDOW_WIDTH;
		int wheight = GameLogic.WINDOW_HEIGHT;
		
		GUI_Container childcontainer = null;
		
		//Create Root Container
		container = new GUI_Container("playergui", 0, 0, wwidth, wheight);
		
		//Create Death Pane
		childcontainer = createDeathPane(container);
		container.addElement(childcontainer);
		
		//Create Dungeon Mini Map
		childcontainer = createDungeonMiniMapPane(container);
        container.addElement(childcontainer);
		
		//Create Character Sheet Pane
		childcontainer = createCharacterSheetPane(container);
		container.addElement(childcontainer);
		
		//Create Inventory Pane
		childcontainer = createInventoryPane(container);
		container.addElement(childcontainer);
		
		//Create Action Bar Pane
		childcontainer = createActionBarPane(container, 0, -ACTIONBARPANE_HEIGHT, wwidth);
		container.addElement(childcontainer);
		
		//Create Abilities Pane
		childcontainer = createAbilitiesPagePane(container);
		container.addElement(childcontainer);
		
		//Create Dungeon Map
		childcontainer = createDungeonMapPane(container);
		container.addElement(childcontainer);
		
		container.setVisible(true);
	}
	
	private GUI_Container createCharacterSheetPane(GUI_Container root) {
		GUI_Container childcontainer = null;
		GUI_Element_DragDropTarget ddt = null;
		GameCharacterListener listener = null;
		GUI_Element_VerticalScrollArea vsa = null;
		GUI_Element element = null;
		UnicodeFont font2 = AssetLoader.font("font_nightserif", 16);
		UnicodeFont font3 = AssetLoader.font("font_receipt", 12); //cd
		
		int charxoffset = 25;
		int charyoffset = 50;
		int charwidth = 300;
		int charheight = 310;
		GUI_Container mainpane = new GUI_Container(root, "charsheetpane", null, charxoffset, -(charyoffset + (charheight / 2)), 
				charwidth, charheight, GUI_Element.RENDER_CONTAINER, false, Anchor.LEFT);
		charSheetPane = mainpane;
		
		//Main Pane Background Image
		element = new GUI_Element_Image(mainpane, "image_background", AssetLoader.image("ui_frame_background05"), null,
				0, 0, charwidth, charheight, GUI_Element.RENDER_IMAGE, true, Anchor.TOPLEFT);
		mainpane.addElement(element);
		
		//Label
		element = new GUI_Element_Label(mainpane, "lbl_charsheet", "Character Sheet", font2, Color.white, null, null, 14, 3, 1, false, 
				GUI_Element.RENDER_LABEL, true, Anchor.TOPLEFT);
		mainpane.addElement(element);
				
		//Close Button
		int closebtnxoffset = 4;
		int closebtnyoffset = 4;
		int closebtnwidth = 17;
		int closebtnheight = 17;
		element = new GUI_Element_Button(mainpane, "btn_close", AssetLoader.image("ui_button_close_normal"), null,
				AssetLoader.image("ui_button_close_highlight"), null, AssetLoader.image("ui_button_close_select"), null, null, false, 
				GUI_Element_Button.ClickType.LEFT, -(closebtnxoffset + closebtnwidth), closebtnyoffset, closebtnwidth, closebtnheight, 
				GUI_Element.RENDER_BUTTON, true, Anchor.TOPRIGHT);
		element.addListener(new ElementListener(){
			@Override
			public void elementActionPerformed() {
				charSheetPane.setVisible(false);
			}			
		});
		mainpane.addElement(element);
		
		// Equipment / Stat Panes
		int panexoffset = 0;
		int paneyoffset = 23;
		int panewidth = charwidth - (2 * panexoffset);
		int paneheight = charheight - (paneyoffset);
			//Equipment
		final GUI_Container equipmentpane = new GUI_Container(mainpane, "equipmentpane", null, panexoffset, paneyoffset, 
				panewidth, paneheight, RENDER_SUBCONTAINER, true, Anchor.TOPLEFT);
		//BG Image
		element = new GUI_Element_Image(equipmentpane, "image_background", AssetLoader.image("ui_equip_background"), null,
				0, 0, panewidth, paneheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
		equipmentpane.addElement(element);		
		mainpane.addElement(equipmentpane);
		
		// Equipment Slots
		final int slotcount = Equipment.INDEX_COUNT;
		for (int idx = 0; idx < slotcount; idx++) {
			final int index = idx;
			final float slotxoffset = EQUIPMENT_OFFSETS[index].getX();
			final float slotyoffset = EQUIPMENT_OFFSETS[index].getY();
			final int slotwidth = (int) EQUIPMENT_SIZES[index].getX();
			final int slotheight = (int) EQUIPMENT_SIZES[index].getY();
			final GameCharacter gamechar = playerGameCharacter;
			
			// Container Slot
			childcontainer = new GUI_Container(equipmentpane, "equipment_slot_"
					+ index, null, slotxoffset - (slotwidth / 2), slotyoffset - (slotheight / 2), slotwidth, slotheight,
					RENDER_SUBCONTAINER + 2, true, Anchor.CENTER);
			equipmentSlots[index] = childcontainer;
			// Slot Button
			element = new GUI_Element_Button(childcontainer,
					"equip_slot_btn_" + index,
					AssetLoader.image("ui_equip_slot_background"), null,
					AssetLoader.image("ui_slot_highlight"),
					AssetLoader.image("ui_inv_slot_border"),
					AssetLoader.image("ui_slot_select"), null, null, true,
					GUI_Element_Button.ClickType.BOTH, 0, 0, slotwidth, slotheight,
					RENDER_SUBCONTAINER + 3, true, Anchor.TOPLEFT);
			final GUI_Element_Button itemStatusButton = (GUI_Element_Button) element;
			childcontainer.addElement(element);
			// Slot Statuses
			element = new GUI_Element_Image(childcontainer, "image_oom", AssetLoader.image("ability_effect_oom"), null,
					0, 0, slotwidth, slotheight, RENDER_SUBCONTAINER + 4, false, Anchor.TOPLEFT);
			childcontainer.addElement(element);
			final GUI_Element itemStatusOom = element;
			//Item Status cd
			element = new GUI_Element_Image(childcontainer, "image_cd", AssetLoader.image("ability_effect_cd"), null,
					0, 0, slotwidth, slotheight, RENDER_SUBCONTAINER + 5, false, Anchor.TOPLEFT);
			childcontainer.addElement(element);
			final GUI_Element itemStatusCdImage = element;
			element = new GUI_Element_Label(childcontainer, "label_cd", "0", font3, Color.yellow, Color.black, 
					null, 0, 0, 1, true, RENDER_SUBCONTAINER + 6, false, Anchor.CENTER);
			childcontainer.addElement(element);
			final GUI_Element itemStatusCdLabel = element;
			// Slot DragDropTarget
			ddt = new GUI_Element_DragDropTarget(childcontainer,
					"equip_slot_ddt_" + index,
					itemStatusButton, true, null, 0, 0,
					slotwidth, slotheight, GUI_Element.RENDER_DRAGDROP,
					true, Anchor.TOPLEFT) {
				@Override
				public boolean allowsStartDrag() {
					return true;
				}

				@Override
				public boolean allowsFinishDragFrom(
						GUI_Element_DragDropTarget e) {
					if (this != e) {
						boolean isSlot = e.getStorage() instanceof Inventory.Slot;
						boolean isEquip = e.getStorage() instanceof EquippableItem;
						
						if (isSlot || isEquip) {
							return true;
						}
					}
					
					return false;
				}

				@Override
				public void performValidFinishDragFrom(
						GUI_Element_DragDropTarget e) {
					boolean isSlot = e.getStorage() instanceof Inventory.Slot;
					boolean isEquip = e.getStorage() instanceof EquippableItem;
					
					if (isSlot) {
						//Attempt to equip the inventory item
						final Inventory.Slot slot = (Inventory.Slot) e.getStorage();
						
						Item item = gamechar.getInventory().getItemInSlot(slot);
						if (item instanceof EquippableItem) {
							EquippableItem equip = (EquippableItem) item;
							
							if (gamechar.getEquipment().isSlotTypeValidTargetForItem(Equipment.indexMapping[index], equip)) {
								gamechar.getEquipment().equipItem((EquippableItem) item, gamechar.getInventory().new Slot(0, index), true);
							}
						}
					}
					else if (isEquip) {
						//Attempt to swap equipment
						final EquippableItem a = (EquippableItem) e.getStorage();
						final EquippableItem b = (EquippableItem) getStorage();
						
						if (b == null) {
							//Target null, no swap
						}
						else {
							//Target not null, try to swap
							gamechar.getEquipment().attemptSwap(a, b, true);
						}
					}
				}

				@Override
				public void finishDraggingContextActions(GUI_Element_DragDropTarget e, Object storage) {
					//DO NOTHING
				}
			};
			ddt.setStorage(gamechar.getInventory().getItemInSlot(gamechar.getInventory().new Slot(0, index)));
			equipmentDragDrops[index] = ddt;
			childcontainer.addElement(ddt);
			equipmentpane.addElement(childcontainer);
			
			//Add Status Listener
			//Status Listener
			listener = new GameCharacterListener(){				
				@Override
				public void eventPerformed(GameCharacterEvent e) {
					final GameCharacter gamechar = e.getGameCharacter();
					final Inventory inventory = gamechar.getInventory();
					final Equipment equipment = gamechar.getEquipment();
					final Inventory.Slot slot = inventory.new Slot(0, index);
					final EquippableItem item = equipment.getItemInSlot(slot);
					
					if (item != null) {
						if (e.getEventType().equals(GameCharacterEvent.Type.Equipment_Modified.getType())) {
							//Equipment Modified
							
							//Set DDT information and Button Foreground
							itemStatusButton.setForegroundImage(item.getIconImage());
							equipmentDragDrops[index].setDragImage(item.getIconImage());
							equipmentDragDrops[index].setStorage(item);
							
							// Clear Button Listener
							itemStatusButton.clearListeners();
							
							//Set Tooltip
							Tooltip ttip = GUI_AdvancedTooltip.createItemTooltip(item);
							itemStatusButton.setTooltip(ttip);
							ttip.setParent(itemStatusButton);
							
							//Check Cooldown
							if (item.getCurrentCooldown() > 0) {
								((GUI_Element_Label) itemStatusCdLabel).setLabel(item.getCurrentCooldown() + "");
								itemStatusCdLabel.setVisible(true);
								itemStatusCdImage.setVisible(true);
							}
							else {
								itemStatusCdLabel.setVisible(false);
								itemStatusCdImage.setVisible(false);
							}
							
							// Add Button Listener
							itemStatusButton.addListener(new ElementListener() {
								@Override
								public void elementActionPerformed() {
									gamechar.useAbility(item);
								}								
							});
						}
						else if (e.getEventType().equals(GameCharacterEvent.Type.Turn_Taken.getType())) {
							//Turn Taken, Check Cooldown
							if (item.getCurrentCooldown() > 0) {
								((GUI_Element_Label) itemStatusCdLabel).setLabel(item.getCurrentCooldown() + "");
								itemStatusCdLabel.setVisible(true);
								itemStatusCdImage.setVisible(true);
							}
							else {
								itemStatusCdLabel.setVisible(false);
								itemStatusCdImage.setVisible(false);
							}
						}
						else if (e.getEventType().equals(GameCharacterEvent.Type.Level_Modified.getType())) {
							//Level modified recreate tooltip
							Tooltip ttip = GUI_AdvancedTooltip.createItemTooltip(item);
							itemStatusButton.setTooltip(ttip);
							ttip.setParent(itemStatusButton);
						}
						else if (e.getEventType().equals(GameCharacterEvent.Type.Stat_CurrentMana_Modified.getType())) {
							//Mana Changed, Check oom
							if (gamechar.getStat(Stat_CurrentMana.REFERENCE).getModifiedValue() < item.getCost()) {
								itemStatusOom.setVisible(true);
							}
							else {
								itemStatusOom.setVisible(false);
							}
						}
					}
					else {
						//Reset to Default
						itemStatusCdLabel.setVisible(false);
						itemStatusCdImage.setVisible(false);
						itemStatusOom.setVisible(false);
						
						//Set DDT information and Button Foreground
						itemStatusButton.setForegroundImage(null);
						equipmentDragDrops[index].setDragImage(null);
						equipmentDragDrops[index].setStorage(item);
						
						// Clear Button Listener
						itemStatusButton.clearListeners();
						
						//Set Tooltip
						itemStatusButton.setTooltip(null);
					}
				}

				@Override
				public List<String> getEventTypes() {
					ArrayList<String> types = new ArrayList<String>();
					//Inventory
					types.add(GameCharacterEvent.Type.Equipment_Modified.getType());
					//Turn
					types.add(GameCharacterEvent.Type.Turn_Taken.getType());
					//Level
					types.add(GameCharacterEvent.Type.Level_Modified.getType());
					//Stats
					types.add(GameCharacterEvent.Type.Stat_CurrentMana_Modified.getType());
					return types;
				}			
			};
				equipmentStatusListeners[index] = listener;
				gamechar.addListener(listener);
			}
		
			//Stats
		final GUI_Container statspane = new GUI_Container(mainpane, "statspane", null, panexoffset, paneyoffset, 
				panewidth, paneheight, RENDER_SUBCONTAINER, false, Anchor.TOPLEFT);
		//BG Image
		element = new GUI_Element_Image(statspane, "image_background", AssetLoader.image("ui_frame_background05"), null,
				0, 0, panewidth, paneheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
		statspane.addElement(element);
		//VerticalScrollArea
		int vsaxoffset = 24;
		int vsayoffset = 15;
		int vsawidth = panewidth - (2 * vsaxoffset);
		int vsaheight = paneheight - (vsayoffset) - 24;
		int vsascrollbuttonsize = 15;
		Image[] scrollup = {
				AssetLoader.image("ui_button_arrowup_normal"), 
				AssetLoader.image("ui_button_arrowup_highlight"), 
				AssetLoader.image("ui_button_arrowup_select")};
		Image[] scrolldown = {
				AssetLoader.image("ui_button_arrowup_normal").getFlippedCopy(true, true), 
				AssetLoader.image("ui_button_arrowup_highlight").getFlippedCopy(true, true), 
				AssetLoader.image("ui_button_arrowup_select").getFlippedCopy(true, true)};
		Image[] scrollbar = {
				AssetLoader.image("ui_scrollbar2_background"), 
				AssetLoader.image("ui_scrollbar2_foreground")};
		vsa = new GUI_Element_VerticalScrollArea(statspane, "vsa_stats", AssetLoader.image("ui_frame_background04"),
				vsaxoffset, vsayoffset, vsawidth, vsaheight, scrollup, scrolldown, scrollbar, vsascrollbuttonsize, 
				RENDER_SUBCONTAINER + 3, true, Anchor.TOPLEFT);
		charSheetVsa = vsa;
		statspane.addElement(vsa);
		//Taper Image
		element = new GUI_Element_Image(statspane, "image_vsa_taper", AssetLoader.image("ui_frame_background04_taper"), null,
				vsaxoffset - 15, vsayoffset - 15, vsawidth + 30, vsaheight + 30, RENDER_SUBCONTAINER + 4, true, Anchor.TOPLEFT);
		statspane.addElement(element);
		charSheetStatsPane = statspane;
		mainpane.addElement(statspane);
		
		//Tab Buttons
		int tabbtnyoffset = 4;
		int tabbtnwidth = charwidth / 2;
		int tabbtnheight = 32;
			//Equipment Tab
		final GUI_Element_Button equiptab = new GUI_Element_Button(mainpane, "btn_equiptab", AssetLoader.image("ui_tab01_equipment_focus"), null,
				null, null, null, null, null, true, false,
				GUI_Element_Button.ClickType.LEFT, 0, -(tabbtnyoffset), tabbtnwidth, tabbtnheight, 
				RENDER_SUBCONTAINER + 2, true, Anchor.BOTLEFT);
		mainpane.addElement(equiptab);
			//Stats Tab
		final GUI_Element_Button statstab = new GUI_Element_Button(mainpane, "btn_statstab", AssetLoader.image("ui_tab01_stats_unfocus"), null,
				null, null, null, null, null, true, false, 
				GUI_Element_Button.ClickType.LEFT, -(tabbtnwidth), -(tabbtnyoffset), tabbtnwidth, tabbtnheight, 
				RENDER_SUBCONTAINER + 2, true, Anchor.BOTRIGHT);
		mainpane.addElement(statstab);
		
		equiptab.addListener(new ElementListener(){
			@Override
			public void elementActionPerformed() {
				equipmentpane.setVisible(true);
				statspane.setVisible(false);
				equiptab.setBackgroundImage(AssetLoader.image("ui_tab01_equipment_focus"));
				statstab.setBackgroundImage(AssetLoader.image("ui_tab01_stats_unfocus"));
			}			
		});
		statstab.addListener(new ElementListener(){
			@Override
			public void elementActionPerformed() {
				statspane.setVisible(true);
				equipmentpane.setVisible(false);
				statstab.setBackgroundImage(AssetLoader.image("ui_tab01_stats_focus"));
				equiptab.setBackgroundImage(AssetLoader.image("ui_tab01_equipment_unfocus"));
			}			
		});
		
		return mainpane;
	}
	
	private GUI_Container createInventoryPane(GUI_Container root) {
		final GUI gui = this;
		final GUI_Container rootpane = root;
		GUI_Container childcontainer;
		GameCharacterListener listener = null;
		GUI_Element element = null;
		GUI_Element_DragDropTarget ddt = null;
		UnicodeFont font2 = AssetLoader.font("font_nightserif", 16);
		UnicodeFont font3 = AssetLoader.font("font_receipt", 12); //cd
		UnicodeFont font4 = AssetLoader.font("font_expressway", 11); //stack
		
		int invxoffset = 25;
		int invyoffset = 50;
		int invwidth = 300;
		int invheight = 310;
		GUI_Container mainpane = new GUI_Container(root, "inventorypane", null, -(invxoffset + invwidth), -(invyoffset + (invheight / 2)), 
				invwidth, invheight, GUI_Element.RENDER_CONTAINER, false, Anchor.RIGHT);
		inventoryPane = mainpane;
		
		//Main Pane Background Image
		element = new GUI_Element_Image(mainpane, "image_background", AssetLoader.image("ui_inv_background"), null,
				0, 0, invwidth, invheight, GUI_Element.RENDER_IMAGE, true, Anchor.TOPLEFT);
		mainpane.addElement(element);
		
		//Label
		element = new GUI_Element_Label(mainpane, "lbl_inventory", "Inventory", font2, Color.white, null, 15, 1, 
				GUI_Element.RENDER_LABEL, true, Anchor.TOPLEFT);
		mainpane.addElement(element);
		
		//Close Button
		int closebtnxoffset = 3;
		int closebtnyoffset = 3;
		int closebtnwidth = 15;
		int closebtnheight = 15;
		element = new GUI_Element_Button(mainpane, "btn_close", AssetLoader.image("ui_button_close_normal"), null,
				AssetLoader.image("ui_button_close_highlight"), null, AssetLoader.image("ui_button_close_select"), null, null, false, 
				GUI_Element_Button.ClickType.LEFT, 
				-(closebtnxoffset + closebtnwidth), closebtnyoffset, closebtnwidth, closebtnheight, 
				GUI_Element.RENDER_BUTTON, true, Anchor.TOPRIGHT);
		element.addListener(new ElementListener(){
			@Override
			public void elementActionPerformed() {
				inventoryPane.setVisible(false);
			}			
		});
		mainpane.addElement(element);
		
		// Inventory Slots
		final int invrows = playerGameCharacter.getInventory().getRowCount();
		final int invcolumns = playerGameCharacter.getInventory().getColumnCount();
		int slotxoffset = 16;
		int slotyoffset = 31;
		int slotxseperator = 4;
		int slotyseperator = 4;
		int slotwidth = 30;
		int slotheight = 30;
		for (int r = 0; r < invrows; r++) {
			for (int c = 0; c < invcolumns; c++) {
				final int row = r;
				final int column = c;
				final GameCharacter gamechar = playerGameCharacter;
				
				// Container Slot
				childcontainer = new GUI_Container(mainpane, "inventory_slot_"
						+ r + "_" + c, null, slotxoffset + (slotwidth * c)
						+ (slotxseperator * c), slotyoffset + (slotheight * r)
						+ (slotyseperator * r), slotwidth, slotheight,
						RENDER_SUBCONTAINER, true, Anchor.TOPLEFT);
				inventorySlots[r][c] = childcontainer;
				// Slot Button
				element = new GUI_Element_Button(childcontainer,
						"inv_slot_btn_" + r + "_" + c,
						AssetLoader.image("ui_inv_slot_background"), null,
						AssetLoader.image("ui_slot_highlight"),
						AssetLoader.image("ui_inv_slot_border"),
						AssetLoader.image("ui_slot_select"), null, null, true,
						GUI_Element_Button.ClickType.BOTH, 0, 0, slotwidth, slotheight,
						RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
				final GUI_Element_Button itemStatusButton = (GUI_Element_Button) element;
				childcontainer.addElement(element);
				// Slot Statuses
				element = new GUI_Element_Image(childcontainer, "image_oom", AssetLoader.image("ability_effect_oom"), null,
						0, 0, slotwidth, slotheight, RENDER_SUBCONTAINER + 2, false, Anchor.TOPLEFT);
				childcontainer.addElement(element);
				final GUI_Element itemStatusOom = element;
				//Item Status cd
				element = new GUI_Element_Image(childcontainer, "image_cd", AssetLoader.image("ability_effect_cd"), null,
						0, 0, slotwidth, slotheight, RENDER_SUBCONTAINER + 3, false, Anchor.TOPLEFT);
				childcontainer.addElement(element);
				final GUI_Element itemStatusCdImage = element;
				element = new GUI_Element_Label(childcontainer, "label_cd", "0", font3, Color.yellow, Color.black, 
						null, 0, 0, 1, true, RENDER_SUBCONTAINER + 4, false, Anchor.CENTER);
				childcontainer.addElement(element);
				final GUI_Element itemStatusCdLabel = element;
				//Item stack label
				element = new GUI_Element_Label(childcontainer, "label_stack", "999", font4, Color.white, Color.black, null,
						7, 7, 1, true, RENDER_SUBCONTAINER + 3, false, Anchor.CENTER);
				inventoryStackLabels[r][c] = (GUI_Element_Label) element;
				childcontainer.addElement(element);
				// Slot DragDropTarget
				ddt = new GUI_Element_DragDropTarget(childcontainer,
						"inv_slot_ddt_" + r + "_" + c,
						itemStatusButton, true, null, 0, 0,
						slotwidth, slotheight, GUI_Element.RENDER_DRAGDROP,
						true, Anchor.TOPLEFT) {
					@Override
					public boolean allowsStartDrag() {
						return true;
					}

					@Override
					public boolean allowsFinishDragFrom(
							GUI_Element_DragDropTarget e) {
						if (this != e) {
							boolean isSlot = e.getStorage() instanceof Inventory.Slot;
							boolean isEquip = e.getStorage() instanceof EquippableItem;
							
							if (isSlot || isEquip) {
								return true;
							}
						}
						
						return false;
					}

					@Override
					public void performValidFinishDragFrom(
							GUI_Element_DragDropTarget e) {
						boolean isSlot = e.getStorage() instanceof Inventory.Slot;
						boolean isEquip = e.getStorage() instanceof EquippableItem;
						
						final Inventory.Slot thisSlot = (Inventory.Slot) getStorage();
						
						if (isSlot) {
							final Inventory.Slot slot = (Inventory.Slot) e.getStorage();
							
							gamechar.getInventory().swapSlots(slot, thisSlot, true);
						}
						else if (isEquip) {
							final EquippableItem item = (EquippableItem) e.getStorage();
							final Item itemInSlot = gamechar.getInventory().getItemInSlot(thisSlot);
							
							if (itemInSlot == null) {
								//Unequip and put in slot
								gamechar.getEquipment().unequipItem(item, true);
								gamechar.getInventory().setItemInSlot(item, thisSlot, true);
							}
							else {
								//Swap inventory with equip
								gamechar.getEquipment().equipItem((EquippableItem) itemInSlot, null, true);
							}
						}
					}

					@Override
					public void finishDraggingContextActions(GUI_Element_DragDropTarget e, Object storage) {
						if (e == null && !gui.hasFocus()) {
							// Only fires if no ddt as recipient, and the game world has focus
							final Inventory.Slot thisSlot = (Inventory.Slot) getStorage();
							final Item item = gamechar.getInventory().getItemInSlot(thisSlot);
							
							//Drop Item Dialog
							ElementListener yesListener = new ElementListener(){
								@Override
								public void elementActionPerformed() {
									//Drop Item, Set Slot to empty.
								    gamechar.getLevel().dropItem(item, gamechar.getRow(), gamechar.getColumn());
									gamechar.getInventory().setItemInSlot(null, thisSlot, true);
								}								
							};
							
							final GUI_Container dropItemDialog = constructQuestionDialog(rootpane, "dropItemDialog", 
									"Are you sure you want to drop " + item.getName() + "?",
									"Drop", "Cancel", yesListener, null);
							rootpane.addElement(dropItemDialog);
						}
					}
				};
				ddt.setStorage(gamechar.getInventory().new Slot(row, column));
				inventoryDragDrops[row][column] = ddt;
				childcontainer.addElement(ddt);
				mainpane.addElement(childcontainer);
				
				//Add Status Listener
				//Status Listener
				listener = new GameCharacterListener(){				
					@Override
					public void eventPerformed(GameCharacterEvent e) {
						final GameCharacter gamechar = e.getGameCharacter();
						final Inventory inventory = gamechar.getInventory();
						final Inventory.Slot slot = inventory.new Slot(row, column);
						final Item item = gamechar.getInventory().getItemInSlot(slot);
						
						if (item != null) {
							if (e.getEventType().equals(GameCharacterEvent.Type.Inventory_Modified.getType())) {
								//Inventory Modified
								
								//Set DDT information and Button Foreground
								itemStatusButton.setForegroundImage(item.getIconImage());
								inventoryDragDrops[row][column].setDragImage(item.getIconImage());
								inventoryDragDrops[row][column].setStorage(slot);
								
								// Clear Button Listener
								itemStatusButton.clearListeners();
								
								//Set Tooltip
								Tooltip ttip = GUI_AdvancedTooltip.createItemTooltip(item);
								itemStatusButton.setTooltip(ttip);
								ttip.setParent(itemStatusButton);
								
								//Check Cooldown
								if (item.getCurrentCooldown() > 0) {
									((GUI_Element_Label) itemStatusCdLabel).setLabel(item.getCurrentCooldown() + "");
									itemStatusCdLabel.setVisible(true);
									itemStatusCdImage.setVisible(true);
								}
								else {
									itemStatusCdLabel.setVisible(false);
									itemStatusCdImage.setVisible(false);
								}
								
								//Set stack information
								if (item.getCurrentStacks() > 1) {
									inventoryStackLabels[row][column].setLabel(item.getCurrentStacks() + "");
									inventoryStackLabels[row][column].setVisible(true);
								}
								else {
									inventoryStackLabels[row][column].setVisible(false);
								}
								
								// Add Button Listener
								itemStatusButton.addListener(new ElementListener() {
									@Override
									public void elementActionPerformed() {
										//Attempt Equip the item if it is equipment, otherwise try to use it (right clicking in equipment uses it)
										if (item instanceof EquippableItem) {
											gamechar.getEquipment().equipItem((EquippableItem) item, null, true);
										}
										else {
											gamechar.useAbility(item);
										}
									}								
								});
							}
							else if (e.getEventType().equals(GameCharacterEvent.Type.Turn_Taken.getType())) {
								//Turn Taken, Check Cooldown
								if (item.getCurrentCooldown() > 0) {
									((GUI_Element_Label) itemStatusCdLabel).setLabel(item.getCurrentCooldown() + "");
									itemStatusCdLabel.setVisible(true);
									itemStatusCdImage.setVisible(true);
								}
								else {
									itemStatusCdLabel.setVisible(false);
									itemStatusCdImage.setVisible(false);
								}
							}
							else if (e.getEventType().equals(GameCharacterEvent.Type.Level_Modified.getType())) {
								//Level modified recreate tooltip
								Tooltip ttip = GUI_AdvancedTooltip.createItemTooltip(item);
								itemStatusButton.setTooltip(ttip);
								ttip.setParent(itemStatusButton);
							}
							else if (e.getEventType().equals(GameCharacterEvent.Type.Stat_CurrentMana_Modified.getType())) {
								//Mana Changed, Check oom
								if (gamechar.getStat(Stat_CurrentMana.REFERENCE).getModifiedValue() < item.getCost()) {
									itemStatusOom.setVisible(true);
								}
								else {
									itemStatusOom.setVisible(false);
								}
							}
						}
						else {
							//Reset to Default
							itemStatusCdLabel.setVisible(false);
							itemStatusCdImage.setVisible(false);
							itemStatusOom.setVisible(false);
							
							//Set DDT information and Button Foreground
							itemStatusButton.setForegroundImage(null);
							inventoryDragDrops[row][column].setDragImage(null);
							inventoryDragDrops[row][column].setStorage(slot);
							
							// Clear Button Listener
							itemStatusButton.clearListeners();
							
							//Set Tooltip
							itemStatusButton.setTooltip(null);
							
							//Hide Stack Label
							inventoryStackLabels[row][column].setVisible(false);
						}
					}

					@Override
					public List<String> getEventTypes() {
						ArrayList<String> types = new ArrayList<String>();
						//Inventory
						types.add(GameCharacterEvent.Type.Inventory_Modified.getType());
						//Turn
						types.add(GameCharacterEvent.Type.Turn_Taken.getType());
						//Level
						types.add(GameCharacterEvent.Type.Level_Modified.getType());
						//Stats
						types.add(GameCharacterEvent.Type.Stat_CurrentMana_Modified.getType());
						return types;
					}			
				};
				inventoryStatusListeners[row][column] = listener;
				playerGameCharacter.addListener(listener);
			}
		}
		
		return mainpane;
	}
	
	private GUI_Container createAbilitiesPagePane(GUI_Container root) {
		GUI_Element_VerticalScrollArea vsa = null;
		GUI_Element element = null;
		UnicodeFont font2 = AssetLoader.font("font_nightserif", 16);
		
		int abilxoffset = 25;
		int abilyoffset = 50;
		int abilwidth = 300;
		int abilheight = 310;
		GUI_Container mainpane = new GUI_Container(root, "abilitiespane", null, abilxoffset, -(abilyoffset + (abilheight / 2)), 
				abilwidth, abilheight, GUI_Element.RENDER_CONTAINER, false, Anchor.LEFT);
		abilitiesPane = mainpane;
		
		//Main Pane Background Image
		element = new GUI_Element_Image(mainpane, "image_dialog_background", AssetLoader.image("ui_dialog01_background"), null,
				0, 0, abilwidth, abilheight, GUI_Element.RENDER_IMAGE, true, Anchor.TOPLEFT);
		mainpane.addElement(element);
		
		//Dialog Header Background Image
		element = new GUI_Element_Image(mainpane, "image_dialog_header_background", AssetLoader.image("ui_dialog01_header_background"), null,
				0, 0, 0, 0, GUI_Element.RENDER_IMAGE, true, Anchor.TOPLEFT);
		mainpane.addElement(element);
		
		//Dialog Footer Background Image
		element = new GUI_Element_Image(mainpane, "image_dialog_footer_background", AssetLoader.image("ui_dialog01_footer_background"), null,
				0, -8, 0, 0, GUI_Element.RENDER_IMAGE + 2, true, Anchor.BOTLEFT);
		mainpane.addElement(element);
		
		//Dialog Left Background Image
		element = new GUI_Element_Image(mainpane, "image_dialog_left_background", AssetLoader.image("ui_dialog01_left_background"), null,
				0, 24, 8, 280, GUI_Element.RENDER_IMAGE, true, Anchor.TOPLEFT);
		mainpane.addElement(element);
		
		//Dialog Right Background Image
		element = new GUI_Element_Image(mainpane, "image_dialog_right_background", AssetLoader.image("ui_dialog01_right_background"), null,
				-8, 24, 8, 282, GUI_Element.RENDER_IMAGE, true, Anchor.TOPRIGHT);
		mainpane.addElement(element);
		
		//Label
		element = new GUI_Element_Label(mainpane, "lbl_abilities", "Abilities", font2, Color.white, null, null, 14, 3, 1, false, 
				GUI_Element.RENDER_LABEL, true, Anchor.TOPLEFT);
		mainpane.addElement(element);
				
		//Close Button
		int closebtnxoffset = 4;
		int closebtnyoffset = 4;
		int closebtnwidth = 17;
		int closebtnheight = 17;
		element = new GUI_Element_Button(mainpane, "btn_close", AssetLoader.image("ui_button_close_normal"), null,
				AssetLoader.image("ui_button_close_highlight"), null, AssetLoader.image("ui_button_close_select"), null, null, false, 
				GUI_Element_Button.ClickType.LEFT, 
				-(closebtnxoffset + closebtnwidth), closebtnyoffset, closebtnwidth, closebtnheight, 
				GUI_Element.RENDER_BUTTON, true, Anchor.TOPRIGHT);
		element.addListener(new ElementListener(){
			@Override
			public void elementActionPerformed() {
				abilitiesPane.setVisible(false);
			}			
		});
		mainpane.addElement(element);
		
		//Vertical Scroll Area
		int vsaxoffset = 15;
		int vsayoffset = 40;
		int vsawidth = abilwidth - (2 * vsaxoffset);
		int vsaheight = abilheight - (2 * vsayoffset);
		int vsascrollbuttonsize = 25;
		Image[] scrollup = {
				AssetLoader.image("ui_button_arrowup_normal"), 
				AssetLoader.image("ui_button_arrowup_highlight"), 
				AssetLoader.image("ui_button_arrowup_select")};
		Image[] scrolldown = {
				AssetLoader.image("ui_button_arrowup_normal").getFlippedCopy(true, true), 
				AssetLoader.image("ui_button_arrowup_highlight").getFlippedCopy(true, true), 
				AssetLoader.image("ui_button_arrowup_select").getFlippedCopy(true, true)};
		Image[] scrollbar = {
				AssetLoader.image("ui_scrollbar_background"), 
				AssetLoader.image("ui_scrollbar_foreground")};
		vsa = new GUI_Element_VerticalScrollArea(mainpane, "vsa_abilities", AssetLoader.image("ui_frame_background02"),
				vsaxoffset, vsayoffset, vsawidth, vsaheight, scrollup, scrolldown, scrollbar, vsascrollbuttonsize, 
				GUI_Element.RENDER_TEXTBOX, true, Anchor.TOPLEFT);
		abilitiesVsa = vsa;
		mainpane.addElement(vsa);
		
		//Dialog Taper Image
		element = new GUI_Element_Image(mainpane, "image_dialog_taper", AssetLoader.image("ui_dialog01_taper"), null,
				vsaxoffset - 8, vsayoffset - 10, vsawidth + 16, vsaheight + 20, GUI_Element.RENDER_TEXTBOX + 1, true, Anchor.TOPLEFT);
		mainpane.addElement(element);
		
		return mainpane;
	}
	
	private GUI_Container createActionBarPane(GUI_Container root, float xoffset, float yoffset, int windoww) {
		final int maxw = 800;
		
		GUI_Container childcontainer;
		GUI_Element element = null;
		GUI_Element element2 = null;
		GUI_Element_MaskImage mask = null;
		GUI_Element_DragDropTarget ddt = null;
		Tooltip ttip = null;
		UnicodeFont font1 = AssetLoader.font("font_coolserif", 12);
		UnicodeFont font2 = AssetLoader.font("font_expressway", 12); //Buff/Debuff Stack font
		UnicodeFont font3 = AssetLoader.font("font_receipt", 9); //Buff/Debuff Duration font
		UnicodeFont font4 = AssetLoader.font("font_expressway", 14);
		UnicodeFont font5 = AssetLoader.font("font_expressway", 12);
		UnicodeFont font6 = AssetLoader.font("font_coolserif", 24); //Cooldown Font
		UnicodeFont font7 = AssetLoader.font("font_receipt", 12);
		
		GUI_Container mainpane = new GUI_Container(root, "actionpane", null, xoffset + (windoww / 2) - (maxw / 2), yoffset, maxw, ACTIONBARPANE_HEIGHT, 
				GUI_Element.RENDER_CONTAINER, true, Anchor.BOTLEFT);
		
		contextTooltipPos = new Vector2f(mainpane.x() + mainpane.getWidth() - 25, mainpane.y() - 5);
		
		//Main Pane Background Image
		element = new GUI_Element_Image(mainpane, "image_background", AssetLoader.image("ui_flair_carvedstonepanel"), null,
				0, 0, maxw, ACTIONBARPANE_HEIGHT, GUI_Element.RENDER_IMAGE, true, Anchor.TOPLEFT);
		mainpane.addElement(element);
		
		//Health Bar Pane
		int hbxoffset = 10;
		int hbyoffset = 5;
		int hbwidth = (maxw / 3) - (2 * hbxoffset);
		int hbheight = 14;
		int mbxoffset = 10;
		int mbyoffset = hbyoffset + hbheight;
		int mbwidth = (maxw / 3) - (2 * mbxoffset);
		int mbheight = 8;
		int hlblyoffset = -4;
		int mlblyoffset = 8;
		childcontainer = new GUI_Container(mainpane, "healthbarPane", null, -(maxw / 6), -30, maxw / 3, 30, RENDER_SUBCONTAINER, true, Anchor.TOP);
		element = new GUI_Element_Image(childcontainer, "image_background", AssetLoader.image("ui_frame_background01"), null,
				0, 0, maxw / 3, 30, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
		childcontainer.addElement(element);
		element = new GUI_Element_Image(childcontainer, "image_health_background", AssetLoader.image("ui_bar_health_background"), null,
				hbxoffset, hbyoffset, hbwidth, hbheight, RENDER_SUBCONTAINER + 2, true, Anchor.TOPLEFT);
		totalHealthImage = (GUI_Element_Image) element;
		childcontainer.addElement(element);
		element = new GUI_Element_MaskImage(childcontainer, "image_health_foreground", AssetLoader.image("ui_bar_health_foreground"), null,
				hbxoffset, hbyoffset, hbwidth, hbheight, RENDER_SUBCONTAINER + 2, true, Anchor.TOPLEFT);
		currentHealthImage = (GUI_Element_MaskImage) element;
		childcontainer.addElement(element);
		element = new GUI_Element_Image(childcontainer, "image_mana_background", AssetLoader.image("ui_bar_mana_background"), null,
				mbxoffset, mbyoffset, mbwidth, mbheight, RENDER_SUBCONTAINER + 2, true, Anchor.TOPLEFT);
		totalManaImage = (GUI_Element_Image) element;
		childcontainer.addElement(element);
		element = new GUI_Element_MaskImage(childcontainer, "image_mana_foreground", AssetLoader.image("ui_bar_mana_foreground"), null,
				mbxoffset, mbyoffset, mbwidth, mbheight, RENDER_SUBCONTAINER + 2, true, Anchor.TOPLEFT);
		currentManaImage = (GUI_Element_MaskImage) element;
		childcontainer.addElement(element);
		element = new GUI_Element_Label(childcontainer, "lbl_health", "NIL / NIL", font4, Color.white, Color.black, null, 0, hlblyoffset, 1,
				true, RENDER_SUBCONTAINER + 3, true, Anchor.CENTER);
		currentHealthLabel = (GUI_Element_Label) element;
		childcontainer.addElement(element);
		element = new GUI_Element_Label(childcontainer, "lbl_mana", "NIL / NIL", font5, Color.white, Color.black, null, 0, mlblyoffset, 1,
				true, RENDER_SUBCONTAINER + 3, true, Anchor.CENTER);
		currentManaLabel = (GUI_Element_Label) element;
		childcontainer.addElement(element);
		mainpane.addElement(childcontainer);
		final GUI_Container healthManaContainer = childcontainer;
		
		//Experience Bar Pane
		int xpxoffset = 0;
		int xpyoffset = 0;
		int xpwidth = mainpane.getWidth();
		int xpheight = 13;
		childcontainer = new GUI_Container(mainpane, "xpbarPane", null, xpxoffset, xpyoffset, xpwidth, xpheight, 
				RENDER_SUBCONTAINER, true, Anchor.TOPLEFT);
		xpPane = childcontainer;		
		element = new GUI_Element_Image(childcontainer, "image_xp_background", AssetLoader.image("ui_bar_xp_background"), null,
				0, 0, xpwidth, xpheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
		childcontainer.addElement(element);		
		element = new GUI_Element_MaskImage(childcontainer, "image_xp_foreground", AssetLoader.image("ui_bar_xp_foreground"), null,
				0, 0, xpwidth, xpheight, RENDER_SUBCONTAINER + 2, true, Anchor.TOPLEFT);
		xpCurrentImage = (GUI_Element_MaskImage) element;
		childcontainer.addElement(element);
		element = new GUI_Element_Image(childcontainer, "image_xp_cursor", AssetLoader.image("ui_bar_xp_cursor"), null,
				0, 0, 7, xpheight, RENDER_SUBCONTAINER + 3, true, Anchor.TOPLEFT);
		xpCursorImage = (GUI_Element_Image) element;
		childcontainer.addElement(element);
		element = new GUI_Element_Image(childcontainer, "image_xp_overlay", AssetLoader.image("ui_bar_xp_overlay"), null,
				0, 0, xpwidth, xpheight, RENDER_SUBCONTAINER + 4, true, Anchor.TOPLEFT);
		childcontainer.addElement(element);
		element = new GUI_Element_Label(childcontainer, "lbl_xp", "NIL / NIL", font7, Color.white, Color.black, null, 0, 0, 1,
				true, RENDER_SUBCONTAINER + 5, true, Anchor.CENTER);
		xpCurrentLabel = (GUI_Element_Label) element;
		childcontainer.addElement(element);
		element = new GUI_Element_RollOverConstraint(childcontainer, "roc_lbl_xp", xpCurrentLabel, 0, 0, xpwidth, xpheight,
				RENDER_SUBCONTAINER + 6, true, Anchor.TOPLEFT);
		childcontainer.addElement(element);
		mainpane.addElement(childcontainer);
		
		//Medusa LevelUp Flair
		mask = new GUI_Element_MaskImage(mainpane, "image_flair_medusafull", AssetLoader.image("ui_flair_medusafull"), null,
				530, -30, 113, 150, GUI_Element.RENDER_IMAGE + 1, true, Anchor.TOPLEFT);
		mask.setMaskHeight(mainpane.getHeight() - xpheight - 24);
		mask.setMaskY(mainpane.y() + xpheight + 12);
		mainpane.addElement(mask);
		ttip = GUI_AdvancedTooltip.createFixedContextTooltip(contextTooltipPos, true, "<1Leveled Up!><br><1Click to allocate.>");
		mask = new GUI_Element_MaskImage(mainpane, "image_flair_medusafullglow", AssetLoader.image("ui_flair_medusafullglow"), ttip,
				530, -30, 113, 150, GUI_Element.RENDER_IMAGE + 2, true, Anchor.TOPLEFT);
		mask.setMaskHeight(mainpane.getHeight() - xpheight - 24);
		mask.setMaskY(mainpane.y() + xpheight + 12);
		ttip.setParent(mask);
		mainpane.addElement(mask);
		element = new GUI_Element_RollOverConstraint(mainpane, "roc_medusa", mask, 530 + 35, xpheight, 113 - 60, 85 - xpheight,
				GUI_Element.RENDER_IMAGE + 3, false, Anchor.TOPLEFT);
		medusaRollOver = (GUI_Element_RollOverConstraint) element;
		mainpane.addElement(element);
		mask = new GUI_Element_MaskImage(mainpane, "image_flair_medusafulloutline", AssetLoader.image("ui_flair_medusafulloutline"), null,
				530, -30, 113, 150, RENDER_SUBCONTAINER + 98, false, Anchor.TOPLEFT);
		mask.setMaskHeight(mainpane.getHeight() - xpheight);
		mask.setMaskY(mainpane.y() + xpheight);
		medusaOutline = mask;
		mainpane.addElement(mask);
		
		//Add ActionButtons
		int btnxseperator = 5;
		int btnxoffset = 15;
		int btnwidth = 50;
		int btnheight = 50;
		String[] bindstrings = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "0"};
		for (int i = 0; i < ACTION_BUTTON_COUNT; i++) {
			final int index = i;
			childcontainer = new GUI_Container(mainpane, "actionbarbtn" + i, null, btnxoffset + (i * btnwidth) + (i * btnxseperator), 
					-(btnheight / 2) + (xpheight / 2), btnwidth, btnheight, RENDER_SUBCONTAINER, true, Anchor.LEFT);
			element = new GUI_Element_Button(childcontainer, "btn_action" + i, AssetLoader.image("ui_slot_background"), null,
					AssetLoader.image("ui_slot_highlight"), AssetLoader.image("ui_slot_border"), AssetLoader.image("ui_slot_select"), null, null, true, 
					GUI_Element_Button.ClickType.BOTH, 0, 0, btnwidth, btnheight, RENDER_SUBCONTAINER, true, Anchor.TOPLEFT);
			final GUI_Element_Button actionButton = (GUI_Element_Button) element;
			actionButtons[i] = (GUI_Element_Button) element;
			childcontainer.addElement(element);
			element2 = new GUI_Element_Label(childcontainer, "lbl_actionbind" + i, bindstrings[i], font1, Color.white, Color.black, null, -10, 0, 
					1, false, RENDER_SUBCONTAINER + 1, true, Anchor.TOPRIGHT);
			//Ability Status oom
			element = new GUI_Element_Image(childcontainer, "image_oom", AssetLoader.image("ability_effect_oom"), null,
					0, 0, btnwidth, btnheight, RENDER_SUBCONTAINER + 2, false, Anchor.TOPLEFT);
			childcontainer.addElement(element);
			final GUI_Element abilityStatusOom = element;
			//Ability Status cd
			element = new GUI_Element_Image(childcontainer, "image_cd", AssetLoader.image("ability_effect_cd"), null,
					0, 0, btnwidth, btnheight, RENDER_SUBCONTAINER + 3, false, Anchor.TOPLEFT);
			childcontainer.addElement(element);
			final GUI_Element abilityStatusCdImage = element;
			element = new GUI_Element_Label(childcontainer, "label_cd", "0", font6, Color.yellow, Color.black, 
					null, 0, 0, 1, true, RENDER_SUBCONTAINER + 4, false, Anchor.CENTER);
			childcontainer.addElement(element);
			final GUI_Element abilityStatusCdLabel = element;
			ddt = new GUI_Element_DragDropTarget(childcontainer, "action_ddt_" + i, actionButton, 
					true, null, 0, 0, btnwidth, btnheight, GUI_Element.RENDER_DRAGDROP, true, Anchor.TOPLEFT) {
						@Override
						public boolean allowsStartDrag() {
							return true;
						}

						@Override
						public boolean allowsFinishDragFrom(
								GUI_Element_DragDropTarget e) {
							if (this != e) {
								boolean isAbility = e.getStorage() instanceof Ability;
								//boolean isItem = e.getStorage() instanceof Item;
								
								if (isAbility) {
									return true;
								}
							}
							
							return false;
						}

						@Override
						public void performValidFinishDragFrom(
								GUI_Element_DragDropTarget e) {
							final Ability ability = (Ability) e.getStorage();
							final GameCharacter gamechar = ability.getOwner();
							
							if (GameLogic.DEBUG) System.out.println("Action Button " + index + " accepted Finish Drag!");
							
							//Clear all listeners from button and status listeners
							actionButton.clearListeners();
							if (actionButtonStatusListeners[index] != null) {
								gamechar.removeListener(actionButtonStatusListeners[index]);
							}
							
							//Set Storage
							setStorage(ability);
							
							//Set Background Image / Drag Image
							actionButton.setForegroundImage(ability.getIconImage());
							setDragImage(ability.getIconImage());
							
							//Set Tooltip
							Tooltip attip = GUI_AdvancedTooltip.createAbilityTooltip(ability.getName(), ability.getTargetType(), 
									ability.getDescription(), ability.getCost(), ability.getTotalCooldown(), (int) ability.getRange());
							attip.setParent(actionButton);
							actionButton.setTooltip(attip);
							
							//Add New Listeners for button and status elements
							actionButton.addListener(new ElementListener() {
								@Override
								public void elementActionPerformed() {
									gamechar.useAbility(ability);
								}								
							});
							actionButtonStatusListeners[index] = new GameCharacterListener() {
								@Override
								public void eventPerformed(GameCharacterEvent e) {
									if (e.getEventType().equals(GameCharacterEvent.Type.Turn_Taken.getType())) {
										//Turn Taken, Check Cooldown
										if (ability.getCurrentCooldown() > 0) {
											((GUI_Element_Label) abilityStatusCdLabel).setLabel(ability.getCurrentCooldown() + "");
											abilityStatusCdLabel.setVisible(true);
											abilityStatusCdImage.setVisible(true);
										}
										else {
											abilityStatusCdLabel.setVisible(false);
											abilityStatusCdImage.setVisible(false);
										}
									}
									else if (e.getEventType().equals(GameCharacterEvent.Type.Level_Modified.getType())) {
										Tooltip ttip = GUI_AdvancedTooltip.createAbilityTooltip(ability.getName(), ability.getTargetType(), 
												ability.getDescription(), ability.getCost(), ability.getTotalCooldown(), (int) ability.getRange());
										actionButton.setTooltip(ttip);
										ttip.setParent(actionButton);
									}
									else if (e.getEventType().equals(GameCharacterEvent.Type.Stat_CurrentMana_Modified.getType())) {
										//Mana Changed, Check oom
										if (gamechar.getStat(Stat_CurrentMana.REFERENCE).getModifiedValue() < ability.getCost()) {
											abilityStatusOom.setVisible(true);
										}
										else {
											abilityStatusOom.setVisible(false);
										}
									}
								}

								@Override
								public List<String> getEventTypes() {
									ArrayList<String> types = new ArrayList<String>();
									//Turn
									types.add(GameCharacterEvent.Type.Turn_Taken.getType());
									//Level
									types.add(GameCharacterEvent.Type.Level_Modified.getType());
									//Stats
									types.add(GameCharacterEvent.Type.Stat_CurrentMana_Modified.getType());
									return types;
								}												
							};
							actionButtonStatusListeners[index].eventPerformed(new GameCharacterEvent(gamechar, 
									GameCharacterEvent.Type.Stat_CurrentMana_Modified.getType()));
							actionButtonStatusListeners[index].eventPerformed(new GameCharacterEvent(gamechar, 
									GameCharacterEvent.Type.Turn_Taken.getType()));
							actionButtonStatusListeners[index].eventPerformed(new GameCharacterEvent(gamechar, 
									GameCharacterEvent.Type.Level_Modified.getType()));
							gamechar.addListener(actionButtonStatusListeners[index]);
						}

						@Override
						public void finishDraggingContextActions(GUI_Element_DragDropTarget e, Object storage) {
							boolean swap = false;
							if (e != null && storage != null) {
								//Check if were swapping
								if (e.isSwappable()) {
									if (allowsFinishDragFrom(e)) {
										swap = true;
									}
								}
							}
							
							if (swap) {
								performValidFinishDragFrom(e);
							}
							else {
								//Clear the slot (Clear Storage, Images, Listeners)
								final Ability ability = (Ability) getStorage();
								final GameCharacter gamechar = ability.getOwner();
								
								//Clear listeners
								actionButton.clearListeners();
								if (actionButtonStatusListeners[index] != null) {
									gamechar.removeListener(actionButtonStatusListeners[index]);
								}
								
								//Clear Storage
								setStorage(null);
								
								//Clear Background Image / Drag Image
								actionButton.setForegroundImage(null);
								setDragImage(null);
								
								//Clear Tooltip
								actionButton.setTooltip(null);
								
								//Clear Status Element Visibility
								abilityStatusCdLabel.setVisible(false);
								abilityStatusCdImage.setVisible(false);
								abilityStatusOom.setVisible(false);
							}
						}
			};
			childcontainer.addElement(element);
			childcontainer.addElement(element2);
			childcontainer.addElement(ddt);
			mainpane.addElement(childcontainer);
		}
		
		//Add Buff Icons
		int iconxseperator = 5;
		int iconyseperator = 15;
		int iconxoffset = -15;
		int iconyoffset = -iconyseperator + 5;
		int iconwidth = 25;
		int iconheight = 25;
		
		int row = 0;
		int column = 0;
		int i = 0;		
		while (i < BUFF_ICON_COUNT) {
			//Add Slot
			childcontainer = new GUI_Container(mainpane, "bufficon" + i, null, 
					healthManaContainer.x() - mainpane.x() + iconxoffset - iconwidth - (iconxseperator * column) - (iconwidth * column), 
					healthManaContainer.y() - mainpane.y() + iconyoffset - (iconyseperator * row) - (iconheight * row), 
					iconwidth, iconheight, RENDER_SUBCONTAINER, false, Anchor.TOPLEFT);
			buffIcons[i] = childcontainer;
			ttip = GUI_AdvancedTooltip.createBuffDebuffTooltip("Buff Tooltip " + i);
			element = new GUI_Element_Image(childcontainer, "image_buff" + i, AssetLoader.image("ui_slot_background"), ttip,
					0, 0, iconwidth, iconheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
			buffIconImages[i] = (GUI_Element_Image) element;
			ttip.setParent(element);
			childcontainer.addElement(element);
			element = new GUI_Element_Image(childcontainer, "image_buffborder" + i, AssetLoader.image("ui_slot_border"), null,
					0, 0, iconwidth, iconheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
			childcontainer.addElement(element);
			element = new GUI_Element_Label(childcontainer, "label_stack" + i, "999", font3, Color.white, Color.black, null,
					-7, -4, 1, true, RENDER_SUBCONTAINER + 2, true, Anchor.BOTRIGHT);
			buffIconStackLabels[i] = (GUI_Element_Label) element;
			childcontainer.addElement(element);
			element = new GUI_Element_Label(childcontainer, "label_duration" + i, "999", font2, new Color(208, 174, 71), Color.black, null,
					0, 5, 1, true, RENDER_SUBCONTAINER + 3, true, Anchor.BOT);
			buffIconDurationLabels[i] = (GUI_Element_Label) element;
			childcontainer.addElement(element);
			mainpane.addElement(childcontainer);
			
			column++;
			if (column >= BUFF_ICON_COLUMN_SIZE) {
				column = 0;
				row++;
			}			
			i++;
		}
		
		iconxoffset = 15;
		row = 0;
		column = 0;
		i = 0;		
		while (i < DEBUFF_ICON_COUNT) {
			//Add Slot
			childcontainer = new GUI_Container(mainpane, "debufficon" + i, null, 
					healthManaContainer.x() - mainpane.x() + healthManaContainer.getWidth() + iconxoffset + (iconxseperator * column) + (iconwidth * column), 
					healthManaContainer.y() - mainpane.y() + iconyoffset - (iconyseperator * row) - (iconheight * row), 
					iconwidth, iconheight, RENDER_SUBCONTAINER, false, Anchor.TOPLEFT);
			debuffIcons[i] = childcontainer;
			ttip = GUI_AdvancedTooltip.createBuffDebuffTooltip("Debuff Tooltip " + i);
			element = new GUI_Element_Image(childcontainer, "image_debuff" + i, AssetLoader.image("ui_slot_background"), ttip,
					0, 0, iconwidth, iconheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
			debuffIconImages[i] = (GUI_Element_Image) element;
			ttip.setParent(element);
			childcontainer.addElement(element);
			element = new GUI_Element_Image(childcontainer, "image_debuffborder" + i, AssetLoader.image("ui_slot_border"), null,
					0, 0, iconwidth, iconheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
			childcontainer.addElement(element);
			element = new GUI_Element_Label(childcontainer, "label_stack" + i, "999", font3, Color.white, Color.black, null,
					-7, -4, 1, true, RENDER_SUBCONTAINER + 2, true, Anchor.BOTRIGHT);
			debuffIconStackLabels[i] = (GUI_Element_Label) element;
			childcontainer.addElement(element);
			element = new GUI_Element_Label(childcontainer, "label_duration" + i, "999", font2, new Color(208, 174, 71), Color.black, null,
					0, 5, 1, true, RENDER_SUBCONTAINER + 3, true, Anchor.BOT);
			debuffIconDurationLabels[i] = (GUI_Element_Label) element;
			childcontainer.addElement(element);
			mainpane.addElement(childcontainer);
			
			column++;
			if (column >= DEBUFF_ICON_COLUMN_SIZE) {
				column = 0;
				row++;
			}			
			i++;
		}
		
		//Add Panel Pane
		int panelxoffset = 20;
		int panelwidth = 160;
		int panelheight = 32;
		btnwidth = panelwidth / 5;
		btnheight = panelheight;
		btnxoffset = 2;
		childcontainer = new GUI_Container(mainpane, "panelpane", null, -(panelxoffset + panelwidth), -(panelheight / 2) + (xpheight / 2), 
				panelwidth, panelheight, RENDER_SUBCONTAINER, true, Anchor.RIGHT);
			//Inventory Button
		ttip = GUI_AdvancedTooltip.createFixedContextTooltip(contextTooltipPos, true, "Inventory <1[B]>");
		element = new GUI_Element_Button(childcontainer, "btn_inventory", 
				AssetLoader.image("ui_slot_background"), AssetLoader.image("ui_icon_inventory"),
				AssetLoader.image("ui_slot_highlight"), AssetLoader.image("ui_slot_goldborder"), AssetLoader.image("ui_slot_select"), 
				ttip, null, false, GUI_Element_Button.ClickType.LEFT, (btnxoffset * 0) + (btnwidth * 0), 0, 
				btnwidth, btnheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
		ttip.setParent(element);
		element.addListener(new ElementListener(){
			@Override
			public void elementActionPerformed() {
				inventoryPane.setVisible(!inventoryPane.isVisible());
				if (dungeonMapPane.isVisible()) dungeonMapPane.setVisible(false);
			}			
		});
		panelButtons[0] = (GUI_Element_Button) element;
		childcontainer.addElement(element);
			//Character Button
		ttip = GUI_AdvancedTooltip.createFixedContextTooltip(contextTooltipPos, true, "Character Sheet <1[C]>");
		element = new GUI_Element_Button(childcontainer, "btn_character",  
				AssetLoader.image("ui_slot_background"), AssetLoader.image("ui_icon_charactersheet"),
				AssetLoader.image("ui_slot_highlight"), AssetLoader.image("ui_slot_goldborder"), AssetLoader.image("ui_slot_select"), 
				ttip, null, false, GUI_Element_Button.ClickType.LEFT, (btnxoffset * 1) + (btnwidth * 1), 0, 
				btnwidth, btnheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
		ttip.setParent(element);
		element.addListener(new ElementListener(){
			@Override
			public void elementActionPerformed() {
				charSheetPane.setVisible(!charSheetPane.isVisible());
				if (abilitiesPane.isVisible()) abilitiesPane.setVisible(false);
				if (dungeonMapPane.isVisible()) dungeonMapPane.setVisible(false);
			}			
		});
		panelButtons[1] = (GUI_Element_Button) element;
		childcontainer.addElement(element);
			//Abilities Button
		ttip = GUI_AdvancedTooltip.createFixedContextTooltip(contextTooltipPos, true, "Abilities Page <1[V]>");
		element = new GUI_Element_Button(childcontainer, "btn_abilities",  
				AssetLoader.image("ui_slot_background"), AssetLoader.image("ui_icon_abilitiespage"),
				AssetLoader.image("ui_slot_highlight"), AssetLoader.image("ui_slot_goldborder"), AssetLoader.image("ui_slot_select"), 
				ttip, null, false, GUI_Element_Button.ClickType.LEFT, (btnxoffset * 2) + (btnwidth * 2), 0, 
				btnwidth, btnheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
		ttip.setParent(element);
		element.addListener(new ElementListener(){
			@Override
			public void elementActionPerformed() {
				abilitiesPane.setVisible(!abilitiesPane.isVisible());
				if (charSheetPane.isVisible()) charSheetPane.setVisible(false);
				if (dungeonMapPane.isVisible()) dungeonMapPane.setVisible(false);
			}			
		});
		panelButtons[2] = (GUI_Element_Button) element;
		childcontainer.addElement(element);
			//Map Button
		ttip = GUI_AdvancedTooltip.createFixedContextTooltip(contextTooltipPos, true, "Dungeon Map <1[TAB]>");
		element = new GUI_Element_Button(childcontainer, "btn_map",  
				AssetLoader.image("ui_slot_background"), AssetLoader.image("ui_icon_dungeonmap"),
				AssetLoader.image("ui_slot_highlight"), AssetLoader.image("ui_slot_goldborder"), AssetLoader.image("ui_slot_select"), 
				ttip, null, false, GUI_Element_Button.ClickType.LEFT, (btnxoffset * 3) + (btnwidth * 3), 0, 
				btnwidth, btnheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
		ttip.setParent(element);
		element.addListener(new ElementListener(){
            @Override
            public void elementActionPerformed() {
                if (!dungeonMapPane2.isVisible() && !dungeonMapPane.isVisible()) {
                    dungeonMapPane.setVisible(true);
                    dungeonMapPane2.setVisible(false);
                    if (abilitiesPane.isVisible()) abilitiesPane.setVisible(false);
                    if (charSheetPane.isVisible()) charSheetPane.setVisible(false);
                    if (inventoryPane.isVisible()) inventoryPane.setVisible(false);
                }
                else if (!dungeonMapPane2.isVisible() && dungeonMapPane.isVisible()) {
                    dungeonMapPane.setVisible(false);
                    dungeonMapPane2.setVisible(true);
                }
                else if (dungeonMapPane2.isVisible() && !dungeonMapPane.isVisible()) {
                    dungeonMapPane.setVisible(false);
                    dungeonMapPane2.setVisible(false);
                }
            }           
        });
		panelButtons[3] = (GUI_Element_Button) element;
		childcontainer.addElement(element);
			//Menu Button
		ttip = GUI_AdvancedTooltip.createFixedContextTooltip(contextTooltipPos, true, "Options Menu <1[ESC]>");
		element = new GUI_Element_Button(childcontainer, "btn_menu",  
				AssetLoader.image("ui_slot_background"), AssetLoader.image("ui_icon_optionsmenu"),
				AssetLoader.image("ui_slot_highlight"), AssetLoader.image("ui_slot_goldborder"), AssetLoader.image("ui_slot_select"), 
				ttip, null, false, GUI_Element_Button.ClickType.LEFT, (btnxoffset * 4) + (btnwidth * 4), 0, 
				btnwidth, btnheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
		ttip.setParent(element);
		panelButtons[4] = (GUI_Element_Button) element;
		childcontainer.addElement(element);
		
		mainpane.addElement(childcontainer);
		
		return mainpane;
	}
	
	/**
	 * Creates the dungeon map
	 */
	private GUI_Container createDungeonMapPane(GUI_Container root) {
        GUI_Element element = null;
        UnicodeFont font2 = AssetLoader.font("font_nightserif", 16);
        
        int dmxoffset = 0;
        int dmyoffset = -50;
        int dmwidth = 760;
        int dmheight = 450;
        GUI_Container mainpane = new GUI_Container(root, "dungeonmappane", null, -(dmwidth / 2) + dmxoffset, -(dmheight / 2) + dmyoffset, 
                dmwidth, dmheight, GUI_Element.RENDER_CONTAINER, false, Anchor.CENTER);
        dungeonMapPane = mainpane;
        
        //Main Pane Background Image
        element = new GUI_Element_Image(mainpane, "image_background", AssetLoader.image("ui_frame_background01"), null,
                0, 0, dmwidth, dmheight, GUI_Element.RENDER_IMAGE, true, Anchor.TOPLEFT);
        mainpane.addElement(element);
        
        //Label
        element = new GUI_Element_Label(mainpane, "lbl_dungeonmap", "Dungeon Map", font2, Color.white, null, 15, 1, 
                GUI_Element.RENDER_LABEL, true, Anchor.TOPLEFT);
        mainpane.addElement(element);
        
        //Close Button
        int closebtnxoffset = 3;
        int closebtnyoffset = 3;
        int closebtnwidth = 15;
        int closebtnheight = 15;
        element = new GUI_Element_Button(mainpane, "btn_close", AssetLoader.image("ui_button_close_normal"), null,
                AssetLoader.image("ui_button_close_highlight"), null, AssetLoader.image("ui_button_close_select"), null, null, false, 
                GUI_Element_Button.ClickType.LEFT, 
                -(closebtnxoffset + closebtnwidth), closebtnyoffset, closebtnwidth, closebtnheight, 
                GUI_Element.RENDER_BUTTON, true, Anchor.TOPRIGHT);
        element.addListener(new ElementListener(){
            @Override
            public void elementActionPerformed() {
                dungeonMapPane.setVisible(false);
            }           
        });
        mainpane.addElement(element);
        
        //Level Image
        int lvlimagexoffset = 10;
        int lvlimageyoffset = 10;
        int lvlimagewidth = dmwidth - (lvlimagexoffset * 2);
        int lvlimageheight = dmheight - (lvlimageyoffset * 2);
        element = new GUI_Element_Image(mainpane, "image_level", null, null,
                lvlimagexoffset, lvlimageyoffset, lvlimagewidth, lvlimageheight, GUI_Element.RENDER_IMAGE, true, Anchor.TOPLEFT);
        dungeonMapLevelImage = (GUI_Element_Image) element;
        mainpane.addElement(element);
        
        return mainpane;
	}
	
	/**
     * Creates the dungeon mini map
     */
    private GUI_Container createDungeonMiniMapPane(GUI_Container root) {
        GUI_Element element = null;
        UnicodeFont font2 = AssetLoader.font("font_nightserif", 16);
        
        int dmxoffset = 10;
        int dmyoffset = 10;
        int dmwidth = 210;
        int dmheight = 210;
        GUI_Container mainpane = new GUI_Container(root, "dungeonmappane", null, -(dmxoffset + dmwidth), dmyoffset, 
                dmwidth, dmheight, GUI_Element.RENDER_CONTAINER, false, Anchor.TOPRIGHT);
        dungeonMapPane2 = mainpane;
        
        //Main Pane Background Image
        element = new GUI_Element_Image(mainpane, "image_background", AssetLoader.image("ui_frame_background01"), null,
                0, 0, dmwidth, dmheight, GUI_Element.RENDER_IMAGE, true, Anchor.TOPLEFT);
        mainpane.addElement(element);
        
        //Label
        element = new GUI_Element_Label(mainpane, "lbl_dungeonmap", "Dungeon Map", font2, Color.white, null, 15, 1, 
                GUI_Element.RENDER_LABEL, true, Anchor.TOPLEFT);
        mainpane.addElement(element);
        
        //Close Button
        int closebtnxoffset = 3;
        int closebtnyoffset = 3;
        int closebtnwidth = 15;
        int closebtnheight = 15;
        element = new GUI_Element_Button(mainpane, "btn_close", AssetLoader.image("ui_button_close_normal"), null,
                AssetLoader.image("ui_button_close_highlight"), null, AssetLoader.image("ui_button_close_select"), null, null, false, 
                GUI_Element_Button.ClickType.LEFT, 
                -(closebtnxoffset + closebtnwidth), closebtnyoffset, closebtnwidth, closebtnheight, 
                GUI_Element.RENDER_BUTTON, true, Anchor.TOPRIGHT);
        element.addListener(new ElementListener(){
            @Override
            public void elementActionPerformed() {
                dungeonMapPane2.setVisible(false);
            }           
        });
        mainpane.addElement(element);
        
        //Level Image
        int lvlimagexoffset = 10;
        int lvlimageyoffset = 10;
        int lvlimagewidth = dmwidth - (lvlimagexoffset * 2);
        int lvlimageheight = dmheight - (lvlimageyoffset * 2);
        element = new GUI_Element_Image(mainpane, "image_level", null, null,
                lvlimagexoffset, lvlimageyoffset, lvlimagewidth, lvlimageheight, GUI_Element.RENDER_IMAGE, true, Anchor.TOPLEFT);
        dungeonMapLevelImage2 = (GUI_Element_Image) element;
        mainpane.addElement(element);
        
        return mainpane;
    }
    
    /**
     * Creates death pane
     */
    private GUI_Container createDeathPane(GUI_Container root) {
        GUI_Element element = null;
        UnicodeFont font2 = AssetLoader.font("font_nightserif", 48);
        UnicodeFont font1 = AssetLoader.font("font_nightserif", 36);
        
        int dmxoffset = 0;
        int dmyoffset = -50;
        int dmwidth = 760;
        int dmheight = 450;
        GUI_Container mainpane = new GUI_Container(root, "dungeonmappane", null, -(dmwidth / 2) + dmxoffset, -(dmheight / 2) + dmyoffset, 
                dmwidth, dmheight, GUI_Element.RENDER_CONTAINER, false, Anchor.CENTER);
        deathPane = mainpane;
        
        element = new GUI_Element_Label(mainpane, "lbl_death", "You Have Died", font2, Color.white, Color.black, 
                null, 0, 0, 1, true, GUI_Element.RENDER_LABEL, true, Anchor.CENTER);
        mainpane.addElement(element);
        
        element = new GUI_Element_Label(mainpane, "lbl_death", "Press Spacebar to Continue...", font1, Color.white, Color.black, 
                null, 0, 50, 1, true, GUI_Element.RENDER_LABEL, true, Anchor.CENTER);
        mainpane.addElement(element);
        
        return mainpane;
    }
	
	/**
	 * Reconstructs the abilities page ability list from the game character's abilities in the event
	 */
	@SuppressWarnings("unchecked")
	private void reconstructAbilitiesVsa(GameCharacterEvent e) {
		GameCharacterListener listener = null;
		GUI_Element_VerticalScrollArea vsa = abilitiesVsa;
		GUI_Element_DragDropTarget ddt = null;
		GUI_Container mainpane = abilitiesPane;
		GUI_Container childcontainer = null;
		GUI_Container childcontainer2 = null;
		Tooltip ttip = null;
		GUI_Element element = null;
		UnicodeFont font1 = AssetLoader.font("font_coolserif", 24); //Cooldown Font
		UnicodeFont font2 = AssetLoader.font("font_nightserif", 18); //flair name Font
		
		//GameCharacter and abilities instances
		final GameCharacter gamechar = e.getGameCharacter();
		ArrayList<Ability> abilities = (ArrayList<Ability>) gamechar.getAbilities(true);
		abilities = (ArrayList<Ability>) abilities.clone();
		Collections.sort(abilities, new Ability.NameComparator());
		
		//Clear Elements
		vsa.clearElements();
		
		//Clear Listeners
		for (GameCharacterListener l : abilityStatusListeners) {
			gamechar.removeListener(l);
		}
		abilityStatusListeners.clear();
		
		//Reconstruct
		int contyoffset = 5;
		int contyseperator = 2;
		int contwidth = vsa.getWidth() - (32);
		int contheight = 75;
		int btnxoffset = 13;
		int btnwidth = 50;
		int btnheight = 50;
		for (int i = 0; i < abilities.size(); i++) {
			final Ability ability = abilities.get(i);
			
			//Ability Flair Container
			childcontainer = new GUI_Container(mainpane, "abilitycont" + i, null, 20, 
					contyoffset + (vsa.y() - mainpane.y()) + (i * contheight) + (i * contyseperator), 
					contwidth, contheight, RENDER_SUBCONTAINER, true, Anchor.TOPLEFT);
			//Ability Flair Container Background
			element = new GUI_Element_Image(childcontainer, "image_background", AssetLoader.image("ui_frame_background03"), null,
					0, 0, contwidth, contheight, RENDER_SUBCONTAINER + 1, true, Anchor.TOPLEFT);
			childcontainer.addElement(element);
			//Ability Flair Name
			element = new GUI_Element_Label(childcontainer, "label_name", ability.getName(), font2, Color.white, Color.black, 
					null, btnxoffset + btnwidth + ((contwidth - btnxoffset - btnwidth) / 2), 0, 1, true, RENDER_SUBCONTAINER + 2, true, Anchor.LEFT);
			childcontainer.addElement(element);
			//Ability Container
			childcontainer2 = new GUI_Container(childcontainer, "ability" + i, null, btnxoffset, -(btnheight / 2), 
					btnwidth, btnheight, RENDER_SUBCONTAINER + 2, true, Anchor.LEFT);
			//Ability Button
			ttip = GUI_AdvancedTooltip.createAbilityTooltip(ability.getName(), ability.getTargetType(), 
					ability.getDescription(), ability.getCost(), ability.getTotalCooldown(), (int) ability.getRange());
			element = new GUI_Element_Button(childcontainer2, "btn_action" + i, AssetLoader.image("ui_mainmenu_image_background"), 
					ability.getIconImage(), AssetLoader.image("ui_slot_highlight"), AssetLoader.image("ui_slot_select"), 
					null, ttip, null, true, GUI_Element_Button.ClickType.BOTH, 0, 0, 
					btnwidth, btnheight, RENDER_SUBCONTAINER + 3, 
					true, Anchor.TOPLEFT);
			ttip.setParent(element);
			element.addListener(new ElementListener() {
				@Override
				public void elementActionPerformed() {
					gamechar.useAbility(ability);
				}				
			});
			childcontainer2.addElement(element);
			final GUI_Element abilityStatusButton = element;
			//Drag Drop Target
			ddt = new GUI_Element_DragDropTarget(childcontainer2, "ability_ddt_" + i, (GUI_Element_Button) element, 
					false, ability.getIconImage(), 0, 0, btnwidth, btnheight, GUI_Element.RENDER_DRAGDROP, true, Anchor.TOPLEFT) {
						@Override
						public boolean allowsStartDrag() {
							return true;
						}

						@Override
						public boolean allowsFinishDragFrom(
								GUI_Element_DragDropTarget e) {
							//Ability Page DDTs accept no incoming drops
							return false;
						}

						@Override
						public void performValidFinishDragFrom(
								GUI_Element_DragDropTarget e) {
							//Do nothing, will never accept an incoming drop.
						}

						@Override
						public void finishDraggingContextActions(GUI_Element_DragDropTarget e, Object storage) {
							//Do nothing, can't clear the ddt button from the abilities page
						}
			};
			ddt.setStorage(ability);
			childcontainer2.addElement(ddt);
			//Ability Status oom
			element = new GUI_Element_Image(childcontainer2, "image_oom", AssetLoader.image("ability_effect_oom"), null,
					0, 0, btnwidth, btnheight, RENDER_SUBCONTAINER + 4, false, Anchor.TOPLEFT);
			childcontainer2.addElement(element);
			final GUI_Element abilityStatusOom = element;
			//Ability Status cd
			element = new GUI_Element_Image(childcontainer2, "image_cd", AssetLoader.image("ability_effect_cd"), null,
					0, 0, btnwidth, btnheight, RENDER_SUBCONTAINER + 5, false, Anchor.TOPLEFT);
			childcontainer2.addElement(element);
			final GUI_Element abilityStatusCdImage = element;
			element = new GUI_Element_Label(childcontainer2, "label_cd", "0", font1, Color.yellow, Color.black, 
					null, 0, 0, 1, true, RENDER_SUBCONTAINER + 6, false, Anchor.CENTER);
			childcontainer2.addElement(element);
			final GUI_Element abilityStatusCdLabel = element;
			//Add sub containers
			childcontainer.addElement(childcontainer2);
			vsa.addElement(childcontainer);
			
			//Status Listener
			listener = new GameCharacterListener(){				
				@Override
				public void eventPerformed(GameCharacterEvent e) {
					if (e.getEventType().equals(GameCharacterEvent.Type.Turn_Taken.getType())) {
						//Turn Taken, Check Cooldown
						if (ability.getCurrentCooldown() > 0) {
							((GUI_Element_Label) abilityStatusCdLabel).setLabel(ability.getCurrentCooldown() + "");
							abilityStatusCdLabel.setVisible(true);
							abilityStatusCdImage.setVisible(true);
						}
						else {
							abilityStatusCdLabel.setVisible(false);
							abilityStatusCdImage.setVisible(false);
						}
					}
					else if (e.getEventType().equals(GameCharacterEvent.Type.Level_Modified.getType())) {
						Tooltip ttip = GUI_AdvancedTooltip.createAbilityTooltip(ability.getName(), ability.getTargetType(), 
								ability.getDescription(), ability.getCost(), ability.getTotalCooldown(), (int) ability.getRange());
						abilityStatusButton.setTooltip(ttip);
						ttip.setParent(abilityStatusButton);
					}
					else if (e.getEventType().equals(GameCharacterEvent.Type.Stat_CurrentMana_Modified.getType())) {
						//Mana Changed, Check oom
						if (gamechar.getStat(Stat_CurrentMana.REFERENCE).getModifiedValue() < ability.getCost()) {
							abilityStatusOom.setVisible(true);
						}
						else {
							abilityStatusOom.setVisible(false);
						}
					}
				}

				@Override
				public List<String> getEventTypes() {
					ArrayList<String> types = new ArrayList<String>();
					//Turn
					types.add(GameCharacterEvent.Type.Turn_Taken.getType());
					//Level
					types.add(GameCharacterEvent.Type.Level_Modified.getType());
					//Stats
					types.add(GameCharacterEvent.Type.Stat_CurrentMana_Modified.getType());
					return types;
				}			
			};
			abilityStatusListeners.add(listener);
			gamechar.addListener(listener);
		}
	}

	/**
	 * Reconstructs the character sheet stats list from the event
	 */
	private void reconstructCharSheetStatsVsa(GameCharacterEvent e) {
		GameCharacterListener listener = null;
		GUI_Element_VerticalScrollArea vsa = charSheetVsa;
		GUI_Container mainpane = charSheetStatsPane;
		GUI_Container childcontainer = null;
		Tooltip ttip = null;
		final UnicodeFont font1 = AssetLoader.font("font_expressway", 14); //Label Font
		final UnicodeFont font2 = AssetLoader.font("font_expressway", 12); //Tooltip Font
		
		//GameCharacter and abilities instances
		final GameCharacter gamechar = e.getGameCharacter();
		ArrayList<Stat> stats = (ArrayList<Stat>) gamechar.getStats(true); //True when not debugging stats
		Collections.sort(stats, new Stat.subGroupComparator());
		
		//Clear Elements
		vsa.clearElements();
		
		//Clear Listeners
		for (GameCharacterListener l : charSheetStatListeners) {
			gamechar.removeListener(l);
		}
		charSheetStatListeners.clear();
		
		//Reconstruct
		int contxoffset = 6;
		int contyoffset = 4;
		int contyseperator = 5;
		int contwidth = vsa.width - (2 * contxoffset);
		int contheight = font2.getLineHeight();
		int renderbase = vsa.getRenderPriority();
		int scrollpadyoffset = contyoffset;
		for (int i = 0; i < stats.size(); i++) {
			final Stat stat = stats.get(i);
			
			childcontainer = new GUI_Container(mainpane, "statcont_" + stat.getReference(), null, 
					contxoffset + (vsa.x() - mainpane.x()), 
					contyoffset + (vsa.y() - mainpane.y()) + (i * contheight) + (i * contyseperator), 
					contwidth, contheight, renderbase + 1, true, Anchor.TOPLEFT);
			
			scrollpadyoffset += contheight + contyseperator;
			
			int value;
			if (stat.isPercentage()) {
				value = (int) Math.floor(stat.getModifiedValue() * 100);
			}
			else {
				value = (int) Math.floor(stat.getModifiedValue());
			}
			
			String labelString = stat.getAestheticName() + ": " + (stat.isPercentage()?(value + "%"):(value + ""));
			
			//Label + tooltip
			ttip = GUI_AdvancedTooltip.createStatTooltip(stat.getDescription());
			final GUI_Element_Label label = new GUI_Element_Label(childcontainer, "lbl_" + stat.getReference(), labelString, font1, Color.white, ttip, 
					0, 0, renderbase + 2, true, Anchor.TOPLEFT);
			ttip.setParent(label);
			
			childcontainer.addElement(label);
			vsa.addElement(childcontainer);
			
			//Status Listener
			listener = new GameCharacterListener(){				
				@Override
				public void eventPerformed(GameCharacterEvent e) {
					if (e.getEventType().equals(GameCharacterEvent.getStatModifiedEventTypeFromReference(stat.getReference()))) {
						Stat statref = e.getGameCharacter().getStat(stat.getReference());
						
						int value;
						if (statref.isPercentage()) {
							value = (int) Math.floor(statref.getModifiedValue() * 100);
						}
						else {
							value = (int) Math.floor(statref.getModifiedValue());
						}
						
						String labelString = statref.getAestheticName() + ": " + (statref.isPercentage()?(value + "%"):(value + ""));
						
						Tooltip ttip = GUI_AdvancedTooltip.createStatTooltip(stat.getDescription());
						label.setLabel(labelString);
						label.setTooltip(ttip);
						ttip.setParent(label);
					}
				}
	
				@Override
				public List<String> getEventTypes() {
					ArrayList<String> types = new ArrayList<String>();
					types.add(GameCharacterEvent.getStatModifiedEventTypeFromReference(stat.getReference()));
					return types;
				}			
			};
			charSheetStatListeners.add(listener);
			gamechar.addListener(listener);
		}
		
		//Add Scroll Pad
		int padheight = contheight * 2;
		childcontainer = new GUI_Container(mainpane, "scrollpadcont", null, 
				contxoffset + (vsa.x() - mainpane.x()), 
				scrollpadyoffset, 
				contwidth, padheight, renderbase + 3, true, Anchor.TOPLEFT);
		vsa.addElement(childcontainer);
	}
	
	private GUI_Container constructQuestionDialog(GUI_Container root, String id, String question, String yes, String no,
			ElementListener yesListener, ElementListener noListener) {
		GUI_Element element = null;
		GUI_Element_Button button = null;
		UnicodeFont font1 = AssetLoader.font("font_expressway", 14);
		UnicodeFont font2 = AssetLoader.font("font_coolserif", 16);
		
		int questionwidth = font1.getWidth(question);
		int btnwidth = font2.getWidth(yes);
		btnwidth = Math.max(btnwidth, font2.getWidth(no));
		btnwidth += 2 * 5;
		int btnheight = 32;
		int dialogwidth = questionwidth + (2 * 15);
		
		if (!popupContainers.containsKey(id)) {
			int dialogheight = 90;
			int btnxoffset = 5;
			int btnyoffset = 15;
			Image btnBackground = AssetLoader.image("ui_button_normal_background");
			Image btnHighlight = AssetLoader.image("ui_button_normal_highlight");
			final GUI_Container dialog = new GUI_Container(root, id, null, -(dialogwidth / 2), -(dialogheight / 2), 
						dialogwidth, dialogheight, RENDER_SUBCONTAINER + 100, true, Anchor.CENTER);
			element = new GUI_Element_Image(dialog, "image_background", AssetLoader.image("ui_frame_background05"), null,
					0, 0, dialogwidth, dialogheight, RENDER_SUBCONTAINER + 101, true, Anchor.TOPLEFT);
			dialog.addElement(element);
			element = new GUI_Element_Label(dialog, "lbl_question", question, font1, Color.white, null, null, 
					0, 15, 1, true, RENDER_SUBCONTAINER + 101, true, Anchor.TOP);
			dialog.addElement(element);
			button = new GUI_Element_Button(dialog, "btn_yes", btnBackground, null, btnHighlight, null, 
					null, null, null, false, GUI_Element_Button.ClickType.LEFT, 
					-btnxoffset - (btnwidth), -btnheight - btnyoffset, btnwidth, btnheight, RENDER_SUBCONTAINER + 102, true, Anchor.BOT);
			if (yesListener != null) {
				button.addListener(yesListener);
			}
			button.addListener(new ElementListener() {
				@Override
				public void elementActionPerformed() {
					dialog.setVisible(false);
				}				
			});
			dialog.addElement(button);
			element = new GUI_Element_Label(dialog, "lbl_yes", yes, font2, Color.white, null, null, 
					-btnxoffset - (btnwidth / 2), -(btnheight / 2) - btnyoffset, 1, true,
					RENDER_SUBCONTAINER + 102, true, Anchor.BOT);
			dialog.addElement(element);
			button = new GUI_Element_Button(dialog, "btn_no", btnBackground, null, btnHighlight, null, 
					null, null, null, false, GUI_Element_Button.ClickType.LEFT, 
					btnxoffset, -btnheight - btnyoffset, btnwidth, btnheight, RENDER_SUBCONTAINER + 103, true, Anchor.BOT);
			if (noListener != null) {
				button.addListener(noListener);
			}
			button.addListener(new ElementListener() {
				@Override
				public void elementActionPerformed() {
					dialog.setVisible(false);
				}				
			});
			dialog.addElement(button);
			element = new GUI_Element_Label(dialog, "lbl_no", no, font2, Color.white, null, null, 
					btnxoffset + (btnwidth / 2), -(btnheight / 2) - btnyoffset, 1, true,
					RENDER_SUBCONTAINER + 103, true, Anchor.BOT);
			dialog.addElement(element);
			
			popupContainers.put(id, dialog);
			
			return dialog;
		}
		else {
			final GUI_Container dialog = popupContainers.get(id);
			
			dialog.setWidth(dialogwidth);
			
			button = (GUI_Element_Button) dialog.getElement("btn_yes");
			button.setSize(btnwidth, btnheight);
			button.clearListeners();
			if (yesListener != null) {
				button.addListener(yesListener);
			}
			button.addListener(new ElementListener() {
				@Override
				public void elementActionPerformed() {
					dialog.setVisible(false);
				}				
			});
			button = (GUI_Element_Button) dialog.getElement("btn_no");
			button.setSize(btnwidth, btnheight);
			button.clearListeners();
			if (noListener != null) {
				button.addListener(noListener);
			}
			button.addListener(new ElementListener() {
				@Override
				public void elementActionPerformed() {
					dialog.setVisible(false);
				}				
			});
			
			((GUI_Element_Label) dialog.getElement("lbl_question")).setLabel(question);
			((GUI_Element_Label) dialog.getElement("lbl_yes")).setLabel(yes);
			((GUI_Element_Label) dialog.getElement("lbl_no")).setLabel(no);
			
			dialog.setVisible(true);
			
			return dialog;
		}
	}
}
