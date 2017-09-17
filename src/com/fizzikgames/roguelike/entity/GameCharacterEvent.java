package com.fizzikgames.roguelike.entity;

/**
 * A GameCharacterEvent holds a type of character event that occured and the character
 * it is occuring for.
 * @author Maxim Tiourin
 * @version 1.00
 */
public class GameCharacterEvent {
	public enum Type {
		Initialized("Initialized"),
		Buried("Buried"),
		Died("Died"),
		Used_Stairs("Used_Stairs"),
		Finished_Stairs("Finished_Stairs"),
		Turn_Taken("Turn_Taken"),
		Position_Modified("Position_Modified"),
		Level_Modified("Property_Level_Modified"),
		Ability_Added("Ability_Added"),
		Buff_Modified("Buff_Modified"),
		Debuff_Modified("Debuff_Modified"),
		Inventory_Modified("Inventory_Modified"),
		Equipment_Modified("Equipment_Modified"),
		Stat_CurrentHealth_Modified("Stat_CurrentHealth_Modified"),
		Stat_CurrentMana_Modified("Stat_CurrentMana_Modified"),
		Stat_CurrentExperience_Modified("Stat_CurrentExperience_Modified"),
		Stat_TotalHealth_Modified("Stat_TotalHealth_Modified"),
		Stat_TotalMana_Modified("Stat_TotalMana_Modified"),
		Stat_TotalExperience_Modified("Stat_TotalExperience_Modified"),
		Stat_HealthRegeneration_Modified("Stat_HealthRegeneration_Modified"),
		Stat_ManaRegeneration_Modified("Stat_ManaRegeneration_Modified"),
		Stat_Strength_Modified("Stat_Strength_Modified"),
		Stat_Dexterity_Modified("Stat_Dexterity_Modified"),
		Stat_Intelligence_Modified("Stat_Intelligence_Modified"),
		Stat_AbilityEffectiveness_Modified("Stat_AbilityEffectiveness_Modified"),
		Stat_BlockAmount_Modified("Stat_BlockAmount_Modified"),
		Stat_BlockChance_Modified("Stat_BlockChance_Modified"),
		Stat_Armor_Modified("Stat_Armor_Modified"),
		Stat_CriticalHitChance_Modified("Stat_CriticalHitChance_Modified"),
		Stat_CriticalHitDamage_Modified("Stat_CriticalHitDamage_Modified"),
		Stat_DodgeChance_Modified("Stat_DodgeChance_Modified"),
		Stat_MagicFind_Modified("Stat_MagicFind_Modified"),
		Stat_IncomingDamage_Modified("Stat_IncomingDamage_Modified"),
		Stat_IncomingDamageReduction_Modified("Stat_IncomingDamageReduction_Modified"),
		Stat_OutgoingMeleeDamage_Modified("Stat_OutgoingMeleeDamage_Modified"),
		Stat_OutgoingRangedDamage_Modified("Stat_OutgoingRangedDamage_Modified"),
		Stat_SightRadius_Modified("Stat_SightRadius_Modified"),
		Stat_TrapSightRadius_Modified("Stat_TrapSightRadius_Modified");
		
		private String s;
		
		private Type(String s) {
			this.s = s;
		}
		
		public String getType() {
			return s;
		}
	}
	private GameCharacter gamechar;
	private String event;
	
	public GameCharacterEvent(GameCharacter gamechar, String event) {
		this.gamechar = gamechar;
		this.event = event;
	}
	
	public GameCharacter getGameCharacter() {
		return gamechar;
	}
	
	public String getEventType() {
		return event;
	}
	
	/**
	 * Returns the event type string of a stat modified event with the given stat reference.
	 * This is helpful for checking from an event type for a non-constant stat.
	 */
	public static String getStatModifiedEventTypeFromReference(String reference) {
		String stripped = new String(reference.replaceAll(" ", ""));
		return "Stat_" + stripped + "_Modified";
	}
}
