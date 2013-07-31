/*******************************************************************************
 * GuiInteractionVillagerAdult.java
 * Copyright (c) 2013 WildBamaBoy.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

package mca;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/**
 * Defines the GUI used to interact with a villager.
 */
@SideOnly(Side.CLIENT)
public class GuiInteractionVillagerAdult extends Gui 
{
	/** An instance of the villager. */
	private EntityVillagerAdult entityVillager;

	/** Hearts value for the player. */
	int hearts;

	//Basic interaction buttons.
	private GuiButton chatButton;
	private GuiButton jokeButton;
	private GuiButton giftButton;
	private GuiButton followButton;
	private GuiButton setHomeButton;
	private GuiButton stayButton;
	private GuiButton specialButton;
	private GuiButton tradeButton;
	private GuiButton monarchButton;

	//Buttons appearing at the top of the screen.
	private GuiButton takeArrangerRingButton;
	private GuiButton takeGiftButton;

	//Buttons for monarchs.
	private GuiButton demandGiftButton;
	private GuiButton executeButton;
	private GuiButton makeKnightButton;
	private GuiButton makePeasantButton;

	//Buttons for workers.
	private GuiButton hireButton;
	private GuiButton dismissButton;
	private GuiButton aidButton;

	//Buttons for priests.
	private GuiButton divorceSpouseButton;
	private GuiButton divorceCoupleButton;
	private GuiButton giveUpBabyButton;
	private GuiButton adoptBabyButton;
	private GuiButton arrangedMarriageButton;
	
	//Buttons for smiths.
	private GuiButton repairButton;

	//Buttons for librarians.
	private GuiButton openSetupButton;

	//Buttons for miners.
	private GuiButton miningButton;
	private GuiButton mineMethodButton;
	private GuiButton mineFindButton;
	private GuiButton mineDirectionButton;
	private GuiButton mineDistanceButton;
	private GuiButton mineStartButton;

	//Buttons for guards.
	private GuiButton combatButton;
	private GuiButton combatMethodButton;
	private GuiButton combatAttackPigsButton;
	private GuiButton combatAttackSheepButton;
	private GuiButton combatAttackCowsButton;
	private GuiButton combatAttackChickensButton;
	private GuiButton combatAttackSpidersButton;
	private GuiButton combatAttackZombiesButton;
	private GuiButton combatAttackSkeletonsButton;
	private GuiButton combatAttackCreepersButton;
	private GuiButton combatAttackEndermenButton;
	private GuiButton combatAttackUnknownButton;

	//Back and exit buttons.
	private GuiButton backButton;
	private GuiButton exitButton;

	//Miner fields.
	private int mineMethod = 1;
	private int mineDirection = 0;
	private int mineDistance = 5;
	private int mineOre = 0;

	//Fields used to help draw text and manipulate buttons on the gui.
	private boolean inSpecialGui = false;
	private boolean inMiningGui = false;
	private boolean inNoSpecialGui = false;
	private boolean inCombatGui = false;
	private boolean inMonarchGui = false;

	/**
	 * Constructor
	 * 
	 * @param 	entity	The entity that is being interacted with.
	 * @param   player	The player interacting with the entity.
	 */
	public GuiInteractionVillagerAdult(EntityVillagerAdult entity, EntityPlayer player)
	{
		super(player);
		entityVillager = entity;
	}

	@Override
	public void initGui()
	{
		buttonList.clear();
		hearts = entityVillager.getHearts(player);
		drawInteractionGui();
	}

	@Override
	protected void actionPerformed(GuiButton button)
	{
		if (button == exitButton)
		{
			close();
		}

		if (!inSpecialGui)
		{
			actionPerformedBase(button);
		}

		else if (inMiningGui)
		{
			actionPerformedMining(button);
		}

		else if (inCombatGui)
		{
			actionPerformedCombat(button);
		}

		else if (inMonarchGui)
		{
			actionPerformedMonarch(button);
		}

		else if (inSpecialGui)
		{
			if (button == backButton)
			{
				drawInteractionGui();
			}

			else
			{
				switch (entityVillager.profession)
				{
				case 0: actionPerformedFarmer(button); break;
				case 1: actionPerformedLibrarian(button); break;
				case 2: actionPerformedPriest(button); break;
				case 3: actionPerformedSmith(button); break;
				case 4: actionPerformedButcher(button); break;
				case 5: actionPerformedGuard(button); break;
				case 6: actionPerformedBaker(button); break;
				case 7: actionPerformedMiner(button); break;
				}
			}
		}

		else if (inNoSpecialGui)
		{
			drawInteractionGui();
		}
	}

	@Override
	public void drawScreen(int i, int j, float f)
	{
		drawDefaultBackground();

		//Draw hearts.
		drawCenteredString(fontRenderer, Localization.getString("gui.info.hearts") + " = " + hearts, width / 2, height / 2 - 100, 0xffffff);

		//Draw mood and trait.
		drawCenteredString(fontRenderer, Localization.getString("gui.info.mood") + entityVillager.mood.getLocalizedValue(), width / 2 - 150, height / 2 - 65, 0xffffff);
		drawCenteredString(fontRenderer, Localization.getString("gui.info.trait") + entityVillager.trait.getLocalizedValue(), width / 2 - 150, height / 2 - 50, 0xffffff);
		
		if (entityVillager.playerMemoryMap.get(player.username) != null)
		{
			/**********************************
			 * Hiring IF block
			 **********************************/
			//If the villager is a peasant...
			if (entityVillager.isPeasant)
			{
				//Draw (Peasant) beside their name if this is the owner player.
				if (entityVillager.monarchPlayerName.equals(player.username))
				{
					drawCenteredString(fontRenderer, entityVillager.getTitle(MCA.instance.getIdOfPlayer(player), true) + " " + Localization.getString("monarch.title.peasant." + entityVillager.gender.toLowerCase() + ".owner"), width / 2, height / 2 - 80, 0xffffff);
				}
				
				//Else draw (Peasant of %Name%) below their name.
				else
				{
					drawCenteredString(fontRenderer, entityVillager.getTitle(MCA.instance.getIdOfPlayer(player), true), width / 2, height / 2 - 80, 0xffffff);
					drawCenteredString(fontRenderer, Localization.getString(entityVillager, "monarch.title.peasant." + entityVillager.gender.toLowerCase() + ".otherplayer", false), width / 2, height / 2 - 60, 0xffffff);
				}
			}
			
			//If the villager is a knight...
			else if (entityVillager.isKnight)
			{
				//Draw (Knight of %Name%) below their name if this is NOT the owner player.
				if (!entityVillager.monarchPlayerName.equals(player.username))
				{
					drawCenteredString(fontRenderer, entityVillager.getTitle(MCA.instance.getIdOfPlayer(player), true), width / 2, height / 2 - 80, 0xffffff);
					drawCenteredString(fontRenderer, Localization.getString(entityVillager, "monarch.title.knight." + entityVillager.gender.toLowerCase() + ".otherplayer", false), width / 2, height / 2 - 60, 0xffffff);
				}
				
				//Else draw their title like normal. It will be changed to Knight.
				else
				{
					drawCenteredString(fontRenderer, entityVillager.getTitle(MCA.instance.getIdOfPlayer(player), true), width / 2, height / 2 - 80, 0xffffff);
				}
			}
			
			//They're not a peasant or a knight, so check if they're hired by this player and place (Hired) beside their name if they are.
			else if (entityVillager.playerMemoryMap.get(player.username).isHired)
			{
				drawCenteredString(fontRenderer, entityVillager.getTitle(MCA.instance.getIdOfPlayer(player), true) + " " + Localization.getString("gui.title.special.hired"), width / 2, height / 2 - 80, 0xffffff);
			}

			//They're not hired by this player. Draw their title like normal.
			else
			{
				drawCenteredString(fontRenderer, entityVillager.getTitle(MCA.instance.getIdOfPlayer(player), true), width / 2, height / 2 - 80, 0xffffff);
			}

			
			/**********************************
			 * Spousal IF block
			 **********************************/
			//Check if they have a spouse...
			EntityBase spouse = entityVillager.familyTree.getInstanceOfRelative(EnumRelation.Spouse);
			if (spouse != null)
			{
				//If they have a villager spouse and the player is related, then draw (Married to %SpouseRelation% %SpouseName%.)
				if (entityVillager.isMarried && spouse.familyTree.idIsRelative(MCA.instance.getIdOfPlayer(player)))
				{
					drawCenteredString(fontRenderer, Localization.getString(player, entityVillager, "gui.info.family.spouse", false), width / 2 , height / 2 - 60, 0xffffff);
				}

				//Workaround for grandchildren.
				else
				{
					drawCenteredString(fontRenderer, Localization.getString(player, entityVillager, "gui.info.family.spouse.unrelated", false), width / 2, height / 2 - 60, 0xffffff);
				}
			}

			//Spouse turned up null, but check if they're a villager spouse or player spouse anyway.
			//If they are, just draw (Married to %SpouseFullName%), which is remembered regardless of if the spouse is present.
			else if (entityVillager.isMarried || entityVillager.isSpouse)
			{
				drawCenteredString(fontRenderer, Localization.getString(player, entityVillager, "gui.info.family.spouse.unrelated", false), width / 2, height / 2 - 60, 0xffffff);
			}

			//They're not married at all. Check to see if they have parents and draw their names.
			else
			{
				List<Integer> parents = entityVillager.familyTree.getEntitiesWithRelation(EnumRelation.Parent);

				if (parents.size() == 2)
				{
					drawCenteredString(fontRenderer, Localization.getString(entityVillager, "gui.info.family.parents", false), width / 2, height / 2 - 60, 0xffffff);
				}
			}

			/**********************************
			 * GUI stability
			 **********************************/
			if (inCombatGui)
			{
				backButton.enabled = true;
				drawCenteredString(fontRenderer, Localization.getString("gui.info.chore.options"), width / 2, 80, 0xffffff);

				combatMethodButton.enabled = false;
				combatAttackPigsButton.enabled = true;
				combatAttackSheepButton.enabled = true;
				combatAttackCowsButton.enabled = true;
				combatAttackChickensButton.enabled = true;
				combatAttackSpidersButton.enabled = true;
				combatAttackZombiesButton.enabled = true;
				combatAttackSkeletonsButton.enabled = true;
				combatAttackCreepersButton.enabled = true;
				combatAttackEndermenButton.enabled = true;
				combatAttackUnknownButton.enabled = true;
			}

			if (inMiningGui)
			{
				backButton.enabled = true;
				drawCenteredString(fontRenderer, Localization.getString("gui.info.chore.options"), width / 2, 80, 0xffffff);

				mineMethodButton.enabled    = false;
				mineDirectionButton.enabled = mineMethod == 1 ? true : false;
				mineDistanceButton.enabled  = mineMethod == 1 ? true : false;
				mineFindButton.enabled      = mineMethod == 0 ? true : false;
			}

			if (inMonarchGui)
			{
				backButton.enabled = true;
			}
		}

		super.drawScreen(i, j, f);
	}

	/**
	 * Draws the base interaction GUI.
	 */
	private void drawInteractionGui()
	{
		buttonList.clear();
		inSpecialGui = false;
		inMiningGui = false;
		inNoSpecialGui = false;
		inCombatGui = false;
		inMonarchGui = false;

		buttonList.add(chatButton    = new GuiButton(1, width / 2 - 90, height / 2 + 20, 60, 20, Localization.getString("gui.button.interact.chat")));
		buttonList.add(jokeButton    = new GuiButton(2, width / 2 - 90, height / 2 + 40, 60, 20, Localization.getString("gui.button.interact.joke")));
		buttonList.add(giftButton    = new GuiButton(3, width / 2 - 90, height / 2 + 60, 60, 20, Localization.getString("gui.button.interact.gift")));
		buttonList.add(followButton  = new GuiButton(4, width / 2 - 30, height / 2 + 20, 60, 20, Localization.getString("gui.button.interact.follow")));
		buttonList.add(stayButton    = new GuiButton(5, width / 2 - 30, height / 2 + 40, 60, 20, Localization.getString("gui.button.interact.stay")));
		buttonList.add(setHomeButton = new GuiButton(6, width / 2 - 30, height / 2 + 60, 60, 20, Localization.getString("gui.button.interact.sethome")));
		buttonList.add(specialButton = new GuiButton(7, width / 2 + 30, height / 2 + 20, 60, 20, Localization.getString("gui.button.special")));

		if (entityVillager.getProfession() != 5)
		{
			buttonList.add(tradeButton = new GuiButton(8, width / 2 + 30, height / 2 + 40, 60, 20, Localization.getString("gui.button.trade")));
		}

		if (entityVillager.hasArrangerRing)
		{
			buttonList.add(takeArrangerRingButton = new GuiButton(8, width / 2 - 60, height / 2 - 20, 120, 20, Localization.getString("gui.button.interact.takearrangerring")));
		}

		else if (entityVillager.playerMemoryMap.get(player.username).hasGift)
		{
			buttonList.add(takeGiftButton = new GuiButton(8, width / 2 - 60, height / 2 - 20, 120, 20, Localization.getString("gui.button.interact.takegift")));
		}

		if (MCA.instance.playerWorldManagerMap.get(player.username).worldProperties.isMonarch)
		{
			if (entityVillager.getProfession() != 5)
			{
				buttonList.add(monarchButton = new GuiButton(9, width / 2 + 30, height / 2 + 60, 60, 20, Localization.getString("monarch.title.monarch")));
			}
			
			else
			{
				buttonList.add(monarchButton = new GuiButton(9, width / 2 + 30, height / 2 + 40, 60, 20, Localization.getString("monarch.title.monarch")));
			}
		}

		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
		backButton.enabled = false;

		if (entityVillager.isFollowing) followButton.displayString = Localization.getString("gui.button.interact.followstop");
		if (entityVillager.isStaying) stayButton.displayString = Localization.getString("gui.button.interact.staystop");
		if (entityVillager.isEntityAlive() && entityVillager.isTrading()) tradeButton.enabled = false;
	}

	/**
	 * Draws the preist's special Gui.
	 */
	private void drawPriestSpecialGui()
	{
		buttonList.clear();
		inSpecialGui = true;

		buttonList.add(divorceSpouseButton = new GuiButton(1, width / 2 - 125, height / 2 + 10, 85, 20, Localization.getString("gui.button.special.priest.divorcespouse")));
		buttonList.add(divorceCoupleButton = new GuiButton(2, width / 2 - 40, height / 2 + 10, 85, 20, Localization.getString("gui.button.special.priest.divorcecouple")));
		buttonList.add(giveUpBabyButton    = new GuiButton(3, width / 2 + 45, height / 2 + 10, 85, 20, Localization.getString("gui.button.special.priest.giveupbaby")));
		buttonList.add(adoptBabyButton     = new GuiButton(4, width / 2 - 125, height / 2 + 30, 85, 20, Localization.getString("gui.button.special.priest.adoptbaby")));
		//FIXME
		//buttonList.add(arrangedMarriageButton = new GuiButton(4, width / 2 - 40, height / 2 + 30, 85, 20, Localization.getString("gui.button.special.priest.arrangedmarriage")));
		
		WorldPropertiesManager manager = MCA.instance.playerWorldManagerMap.get(Minecraft.getMinecraft().thePlayer.username);
		divorceSpouseButton.enabled = manager.worldProperties.playerSpouseID != 0;
		giveUpBabyButton.enabled = manager.worldProperties.babyExists;
		//arrangedMarriageButton.enabled = manager.worldProperties.playerSpouseID == 0;
		
		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
		backButton.enabled = true;
	}

	/**
	 * Draws the miner's special Gui.
	 */
	private void drawMinerSpecialGui() 
	{
		buttonList.clear();
		inSpecialGui = true;

		buttonList.add(hireButton    = new GuiButton(1, width / 2 - 40, height / 2 - 10, 85, 20, Localization.getString("gui.button.special.guard.hire")));
		buttonList.add(dismissButton = new GuiButton(2, width / 2 - 40, height / 2 + 10, 85, 20, Localization.getString("gui.button.special.guard.dismiss")));
		buttonList.add(miningButton  = new GuiButton(3, width / 2 - 40, height / 2 + 30, 85, 20, Localization.getString("gui.button.chore.mining")));

		if (entityVillager.isInChoreMode)
		{
			miningButton.displayString = Localization.getString("gui.button.child.stopchore");
		}

		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
		backButton.enabled    = true;
		
		if (entityVillager.playerMemoryMap.get(player.username).isHired || entityVillager.isPeasant)
		{
			miningButton.enabled = true;
			hireButton.enabled = false;
			
			if (entityVillager.isPeasant)
			{
				dismissButton.enabled = false;
			}
		}
		
		if (entityVillager.isPeasant && entityVillager.monarchPlayerName.equals(player.username))
		{
			miningButton.enabled = true;
		}
	}

	/**
	 * Draws the baker's special Gui.
	 */
	private void drawBakerSpecialGui() 
	{
		buttonList.clear();
		inSpecialGui = true;

		buttonList.add(aidButton = new GuiButton(1, width / 2 - 40, height / 2 + 30, 85, 20, Localization.getString("gui.button.special.baker.aid")));

		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
		backButton.enabled = true;
	}

	/**
	 * Draws the guard's special Gui.
	 */
	private void drawGuardSpecialGui() 
	{
		buttonList.clear();
		inSpecialGui = true;

		buttonList.add(hireButton    = new GuiButton(1, width / 2 - 40, height / 2 - 10, 85, 20, Localization.getString("gui.button.special.guard.hire")));
		buttonList.add(dismissButton = new GuiButton(2, width / 2 - 40, height / 2 + 10, 85, 20, Localization.getString("gui.button.special.guard.dismiss")));
		buttonList.add(combatButton = new GuiButton(3, width / 2 - 40, height / 2 + 30, 85, 20, Localization.getString("gui.button.chore.combat")));

		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));

		backButton.enabled = true;
		hireButton.enabled = entityVillager.playerMemoryMap.get(player.username).isHired == false;
		dismissButton.enabled = entityVillager.playerMemoryMap.get(player.username).isHired == true;
		combatButton.enabled = entityVillager.playerMemoryMap.get(player.username).isHired || (entityVillager.isKnight && entityVillager.monarchPlayerName.equals(player.username));
	}

	/**
	 * Draws the butcher's special Gui.
	 */
	private void drawButcherSpecialGui() 
	{
		buttonList.clear();
		inSpecialGui = true;

		buttonList.add(aidButton = new GuiButton(1, width / 2 - 40, height / 2 + 30, 85, 20, Localization.getString("gui.button.special.butcher.aid")));

		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
		backButton.enabled = true;
	}

	/**
	 * Draws the smith's special Gui.
	 */
	private void drawSmithSpecialGui() 
	{
		buttonList.clear();
		inSpecialGui = true;

		buttonList.add(aidButton = new GuiButton(1, width / 2 - 40, height / 2 + 30, 85, 20, Localization.getString("gui.button.special.butcher.aid")));

		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
		backButton.enabled = true;
	}

	/**
	 * Draws the farmer's special Gui.
	 */
	private void drawFarmerSpecialGui()
	{
		buttonList.clear();
		inSpecialGui = true;

		buttonList.add(aidButton = new GuiButton(1, width / 2 - 40, height / 2 + 30, 85, 20, Localization.getString("gui.button.special.farmer.aid")));

		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
		backButton.enabled = true;
	}

	/**
	 * Draws the librarian's special Gui.
	 */
	private void drawLibrarianSpecialGui()
	{
		buttonList.clear();
		inSpecialGui = true;

		buttonList.add(openSetupButton = new GuiButton(1, width / 2 - 40, height / 2 + 30, 85, 20, Localization.getString("gui.button.special.librarian.setup")));

		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
		backButton.enabled = true;
	}

	/**
	 * Draws a Gui stating that this person doesn't have special abilities.
	 */
	private void drawNoSpecialGui() 
	{
		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
		backButton.enabled = true;
	}

	/**
	 * Draws the Gui used to control the miner.
	 */
	private void drawMiningGui()
	{
		buttonList.clear();
		inMiningGui = true;

		buttonList.add(mineStartButton    = new GuiButton(1, width / 2 - 40, height / 2 + 85, 85, 20, Localization.getString("gui.button.chore.start")));
		buttonList.add(mineMethodButton    = new GuiButton(2, width / 2 - 70, height / 2 - 30, 135, 20, Localization.getString("gui.button.chore.mining.method")));
		buttonList.add(mineDirectionButton = new GuiButton(3, width / 2 - 70, height / 2 + 10, 135, 20, Localization.getString("gui.button.chore.mining.direction")));
		buttonList.add(mineDistanceButton  = new GuiButton(4, width / 2 - 70, height / 2 + 30, 135, 20, Localization.getString("gui.button.chore.mining.distance") +  mineDistance));
		buttonList.add(mineFindButton      = new GuiButton(5, width / 2 - 70, height / 2 + 50, 135, 20, Localization.getString("gui.button.chore.mining.find")));

		switch (mineMethod)
		{
		case 0: mineMethodButton.displayString += Localization.getString("gui.button.chore.mining.method.passive"); break;
		case 1: mineMethodButton.displayString += Localization.getString("gui.button.chore.mining.method.active"); break;
		}

		switch (mineDirection)
		{
		case 0: mineDirectionButton.displayString += Localization.getString("gui.button.chore.mining.direction.forward"); break;
		case 1: mineDirectionButton.displayString += Localization.getString("gui.button.chore.mining.direction.backward"); break;
		case 2: mineDirectionButton.displayString += Localization.getString("gui.button.chore.mining.direction.left"); break;
		case 3: mineDirectionButton.displayString += Localization.getString("gui.button.chore.mining.direction.right"); break;
		}

		switch (mineOre)
		{
		case 0: mineFindButton.displayString += Localization.getString("gui.button.chore.mining.find.coal"); break;
		case 1: mineFindButton.displayString += Localization.getString("gui.button.chore.mining.find.iron"); break;
		case 2: mineFindButton.displayString += Localization.getString("gui.button.chore.mining.find.lapis"); break;
		case 3: mineFindButton.displayString += Localization.getString("gui.button.chore.mining.find.gold"); break;
		case 4: mineFindButton.displayString += Localization.getString("gui.button.chore.mining.find.diamond"); break;
		case 5: mineFindButton.displayString += Localization.getString("gui.button.chore.mining.find.redstone"); break;
		case 6: mineFindButton.displayString += Localization.getString("gui.button.chore.mining.find.emerald"); break;
		}

		mineMethodButton.enabled = false;
		mineDirectionButton.enabled = false;
		mineDistanceButton.enabled = false;
		mineFindButton.enabled = false;

		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
		backButton.enabled = false;
	}

	/**
	 * Draws the trading Gui.
	 */
	private void drawTradeGui()
	{

	}

	/**
	 * Draws the combat GUI.
	 */
	private void drawCombatGui()
	{
		inCombatGui = true;
		buttonList.clear();

		buttonList.add(combatMethodButton 			= new GuiButton(1, width / 2 - 190, height / 2 - 20, 120, 20, Localization.getString("gui.button.chore.combat.method")));
		buttonList.add(combatAttackPigsButton		= new GuiButton(2, width / 2 - 190, height / 2, 120, 20, Localization.getString("gui.button.chore.combat.attack.pig")));
		buttonList.add(combatAttackSheepButton 		= new GuiButton(3, width / 2 - 190, height / 2 + 20, 120, 20, Localization.getString("gui.button.chore.combat.attack.sheep")));
		buttonList.add(combatAttackCowsButton 		= new GuiButton(4, width / 2 - 190, height / 2 + 40, 120, 20, Localization.getString("gui.button.chore.combat.attack.cow")));
		buttonList.add(combatAttackChickensButton 	= new GuiButton(5, width / 2 - 190, height / 2 + 60, 120, 20, Localization.getString("gui.button.chore.combat.attack.chicken")));
		buttonList.add(combatAttackSpidersButton 	= new GuiButton(6, width / 2 - 60, height / 2 - 20, 120, 20, Localization.getString("gui.button.chore.combat.attack.spider")));
		buttonList.add(combatAttackZombiesButton 	= new GuiButton(7, width / 2 - 60, height / 2, 120, 20, Localization.getString("gui.button.chore.combat.attack.zombie")));
		buttonList.add(combatAttackSkeletonsButton 	= new GuiButton(8, width / 2 - 60, height / 2 + 20, 120, 20, Localization.getString("gui.button.chore.combat.attack.skeleton")));
		buttonList.add(combatAttackCreepersButton 	= new GuiButton(9, width / 2 - 60, height / 2 + 40, 120, 20, Localization.getString("gui.button.chore.combat.attack.creeper")));
		buttonList.add(combatAttackEndermenButton 	= new GuiButton(10, width / 2 - 60, height / 2 + 60, 120, 20, Localization.getString("gui.button.chore.combat.attack.enderman")));
		buttonList.add(combatAttackUnknownButton 	= new GuiButton(11, width / 2 + 80, height / 2 - 20, 120, 20, Localization.getString("gui.button.chore.combat.attack.unknown")));

		if (entityVillager.combatChore.useMelee && entityVillager.combatChore.useRange)
		{
			combatMethodButton.displayString = combatMethodButton.displayString + Localization.getString("gui.button.chore.combat.method.both");
		}

		else if (entityVillager.combatChore.useMelee)
		{
			combatMethodButton.displayString = combatMethodButton.displayString + Localization.getString("gui.button.chore.combat.method.melee");
		}

		else if (entityVillager.combatChore.useRange)
		{
			combatMethodButton.displayString = combatMethodButton.displayString + Localization.getString("gui.button.chore.combat.method.range");
		}

		else
		{
			combatMethodButton.displayString = combatMethodButton.displayString + Localization.getString("gui.button.chore.combat.method.neither");
		}

		combatAttackPigsButton.displayString      += (entityVillager.combatChore.attackPigs)      ? Localization.getString("gui.button.yes") : Localization.getString("gui.button.no");
		combatAttackSheepButton.displayString     += (entityVillager.combatChore.attackSheep)     ? Localization.getString("gui.button.yes") : Localization.getString("gui.button.no");
		combatAttackCowsButton.displayString      += (entityVillager.combatChore.attackCows)      ? Localization.getString("gui.button.yes") : Localization.getString("gui.button.no");
		combatAttackChickensButton.displayString  += (entityVillager.combatChore.attackChickens)  ? Localization.getString("gui.button.yes") : Localization.getString("gui.button.no");
		combatAttackSpidersButton.displayString   += (entityVillager.combatChore.attackSpiders)   ? Localization.getString("gui.button.yes") : Localization.getString("gui.button.no");
		combatAttackZombiesButton.displayString   += (entityVillager.combatChore.attackZombies)   ? Localization.getString("gui.button.yes") : Localization.getString("gui.button.no");
		combatAttackSkeletonsButton.displayString += (entityVillager.combatChore.attackSkeletons) ? Localization.getString("gui.button.yes") : Localization.getString("gui.button.no");
		combatAttackCreepersButton.displayString  += (entityVillager.combatChore.attackCreepers)  ? Localization.getString("gui.button.yes") : Localization.getString("gui.button.no");
		combatAttackEndermenButton.displayString  += (entityVillager.combatChore.attackEndermen)  ? Localization.getString("gui.button.yes") : Localization.getString("gui.button.no");
		combatAttackUnknownButton.displayString   += (entityVillager.combatChore.attackUnknown)   ? Localization.getString("gui.button.yes") : Localization.getString("gui.button.no");

		combatMethodButton.enabled = false;
		combatAttackPigsButton.enabled = false;
		combatAttackSheepButton.enabled = false;
		combatAttackCowsButton.enabled = false;
		combatAttackChickensButton.enabled = false;
		combatAttackSpidersButton.enabled = false;
		combatAttackZombiesButton.enabled = false;
		combatAttackSkeletonsButton.enabled = false;
		combatAttackCreepersButton.enabled = false;
		combatAttackEndermenButton.enabled = false;
		combatAttackUnknownButton.enabled = false;

		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
		backButton.enabled = false;
	}

	/**
	 * Draws the monarch GUI.
	 */
	private void drawMonarchGui()
	{
		buttonList.clear();
		inSpecialGui = true;
		inMonarchGui = true;

		buttonList.add(executeButton	 = new GuiButton(1, width / 2 - 60, height / 2 - 20, 120, 20, Localization.getString("monarch.gui.button.interact.execute")));
		buttonList.add(demandGiftButton  = new GuiButton(2, width / 2 - 60, height / 2 - 0, 120, 20, Localization.getString("monarch.gui.button.interact.demandgift")));
		buttonList.add(makePeasantButton = new GuiButton(3, width / 2 - 60, height / 2 + 20, 120, 20, Localization.getString("monarch.gui.button.interact.makepeasant")));
		buttonList.add(makeKnightButton  = new GuiButton(4, width / 2 - 60, height / 2 + 40, 120, 20, Localization.getString("monarch.gui.button.interact.makeknight")));

		if (entityVillager.profession == 5)
		{
			makePeasantButton.enabled = false;
			
			if (entityVillager.isKnight)
			{
				makeKnightButton.enabled = false;
			}
		}

		else if (entityVillager.profession != 5)
		{
			makeKnightButton.enabled = false;
			
			if (entityVillager.isPeasant)
			{
				makePeasantButton.enabled = false;
			}
		}

		buttonList.add(backButton = new GuiButton(10, width / 2 - 190, height / 2 + 85, 65, 20, Localization.getString("gui.button.back")));
		buttonList.add(exitButton = new GuiButton(11, width / 2 + 125, height / 2 + 85, 65, 20, Localization.getString("gui.button.exit")));
		backButton.enabled = false;
	}

	/**
	 * Handles an action performed in the base GUI.
	 * 
	 * @param	button	The button that was pressed.
	 */
	private void actionPerformedBase(GuiButton button)
	{
		if (button == chatButton)
		{
			entityVillager.doChat(player);
			close();
		}

		else if (button == jokeButton)
		{
			entityVillager.doJoke(player);
			close();
		}

		else if (button == giftButton)
		{
			entityVillager.playerMemoryMap.get(player.username).isInGiftMode = true;
			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "playerMemoryMap", entityVillager.playerMemoryMap));
			close();
		}

		else if (button == followButton)
		{
			if (!entityVillager.isSpouse || (entityVillager.isSpouse && entityVillager.familyTree.idIsRelative(MCA.instance.getIdOfPlayer(player))))
			{
				if (entityVillager.profession == 5)
				{
					if (entityVillager.isKnight)
					{
						if (!entityVillager.monarchPlayerName.equals(player.username))
						{
							entityVillager.say(Localization.getString(player, entityVillager, "monarch.knight.follow.refuse", false));
							close();
						}
						
						else
						{
							if (!entityVillager.isFollowing)
							{
								entityVillager.isFollowing = true;
								entityVillager.isStaying = false;
								entityVillager.followingPlayer = player.username;

								PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isFollowing", true));
								PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isStaying", false));
								PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "followingPlayer", player.username));

								entityVillager.say(Localization.getString(player, entityVillager, "monarch.knight.follow.start", false));
								close();
							}

							else
							{
								entityVillager.isFollowing = false;
								entityVillager.isStaying = false;
								entityVillager.followingPlayer = "None";

								PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isFollowing", false));
								PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isStaying", false));
								PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "followingPlayer", "None"));

								entityVillager.say(Localization.getString(player, entityVillager, "monarch.knight.follow.stop", false));
							}
							
							close();
						}
					}
					
					//They're not a knight and they're not hired.
					else if (entityVillager.playerMemoryMap.get(player.username).isHired == false)
					{
						entityVillager.say(Localization.getString("guard.follow.refuse"));
						close();
					}
					
					//They're not a knight and they're hired.
					else
					{
						if (!entityVillager.isFollowing)
						{
							entityVillager.isFollowing = true;
							entityVillager.isStaying = false;
							entityVillager.followingPlayer = player.username;

							PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isFollowing", true));
							PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isStaying", false));
							PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "followingPlayer", player.username));

							entityVillager.say(Localization.getString(player, entityVillager, "follow.start"));
							close();
						}

						else
						{
							entityVillager.isFollowing = false;
							entityVillager.isStaying = false;
							entityVillager.followingPlayer = "None";

							PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isFollowing", false));
							PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isStaying", false));
							PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "followingPlayer", "None"));

							entityVillager.say(Localization.getString(player, entityVillager, "follow.stop"));
						}
						
						close();
					}
				}

				else if (!entityVillager.isFollowing)
				{
					entityVillager.isFollowing = true;
					entityVillager.isStaying = false;
					entityVillager.followingPlayer = player.username;

					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isFollowing", true));
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isStaying", false));
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "followingPlayer", player.username));

					entityVillager.say(Localization.getString(player, entityVillager, "follow.start"));
					close();
				}

				else
				{
					entityVillager.isFollowing = false;
					entityVillager.isStaying = false;
					entityVillager.followingPlayer = "None";

					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isFollowing", false));
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isStaying", false));
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "followingPlayer", "None"));

					entityVillager.say(Localization.getString(player, entityVillager, "follow.stop"));
				}
			}

			else
			{
				entityVillager.notifyPlayer(player, Localization.getString("multiplayer.interaction.reject.spouse"));
			}

			close();
		}

		else if (button == stayButton)
		{
			if (!entityVillager.isSpouse || (entityVillager.isSpouse && entityVillager.familyTree.idIsRelative(MCA.instance.getIdOfPlayer(player))))
			{
				entityVillager.isStaying = !entityVillager.isStaying;
				entityVillager.isFollowing = false;

				PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isStaying", entityVillager.isStaying));
				PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isFollowing", false));
			}

			else
			{
				entityVillager.notifyPlayer(player, Localization.getString("multiplayer.interaction.reject.spouse"));
			}

			close();
		}

		else if (button == setHomeButton)
		{
			if (!entityVillager.isSpouse || (entityVillager.isSpouse && entityVillager.familyTree.idIsRelative(MCA.instance.getIdOfPlayer(player))))
			{
				entityVillager.homePointX = entityVillager.posX;
				entityVillager.homePointY = entityVillager.posY;
				entityVillager.homePointZ = entityVillager.posZ;
				entityVillager.hasHomePoint = true;

				PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "homePointX", entityVillager.posX));
				PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "homePointY", entityVillager.posY));
				PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "homePointZ", entityVillager.posZ));
				PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "hasHomePoint", true));

				entityVillager.testNewHomePoint();
			}

			else
			{
				entityVillager.notifyPlayer(player, Localization.getString("multiplayer.interaction.reject.spouse"));
			}

			close();
		}

		else if (button == specialButton)
		{
			switch (entityVillager.profession)
			{
			case 0: drawFarmerSpecialGui(); break;
			case 1: drawLibrarianSpecialGui(); break;
			case 2: drawPriestSpecialGui(); break;
			case 3: drawSmithSpecialGui(); break;
			case 4: drawButcherSpecialGui(); break;
			case 5: drawGuardSpecialGui(); break;
			case 6: drawBakerSpecialGui(); break;
			case 7: drawMinerSpecialGui(); break;
			}
		}

		else if (button == takeArrangerRingButton)
		{
			WorldPropertiesManager manager = MCA.instance.playerWorldManagerMap.get(Minecraft.getMinecraft().thePlayer.username);

			entityVillager.hasArrangerRing = false;

			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "hasArrangerRing", false));
			PacketDispatcher.sendPacketToServer(PacketCreator.createDropItemPacket(entityVillager.entityId, MCA.instance.itemArrangersRing.itemID, 1));
			manager.worldProperties.arrangerRingHolderID = 0;
			manager.saveWorldProperties();
			close();
		}

		else if (button == takeGiftButton)
		{
			PlayerMemory memory = entityVillager.playerMemoryMap.get(player.username);
			memory.hasGift = false;
			entityVillager.playerMemoryMap.put(player.username, memory);

			ItemStack giftStack = Logic.getGiftStackFromRelationship(player, entityVillager);

			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "playerMemoryMap", entityVillager.playerMemoryMap));
			PacketDispatcher.sendPacketToServer(PacketCreator.createDropItemPacket(entityVillager.entityId, giftStack.itemID, giftStack.stackSize));
			close();
		}

		else if (button == tradeButton)
		{
			if (entityVillager.isEntityAlive() && !entityVillager.isTrading())
			{
				PacketDispatcher.sendPacketToServer(PacketCreator.createTradePacket(entityVillager));
				close();
			}
		}

		else if (button == monarchButton)
		{
			drawMonarchGui();
		}
	}

	/**
	 * Handles an action performed in the priest's special GUI.
	 * 
	 * @param	button	The button that was pressed.
	 */
	private void actionPerformedPriest(GuiButton button)
	{
		WorldPropertiesManager manager = MCA.instance.playerWorldManagerMap.get(Minecraft.getMinecraft().thePlayer.username);

		if (button == divorceSpouseButton)
		{
			EntityBase playerSpouse = Logic.getEntityWithIDWithinDistance(player, manager.worldProperties.playerSpouseID, 10);

			try
			{
				if (playerSpouse != null)
				{	
					EntityVillagerAdult spouse = (EntityVillagerAdult)playerSpouse;
					spouse.shouldDivorce = true;
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(spouse.entityId, "shouldDivorce", true));
				}

				else //The spouse is not nearby.
				{
					EntityVillagerAdult spouse = null;

					for (Object obj : entityVillager.worldObj.loadedEntityList)
					{
						if (obj instanceof EntityBase)
						{
							EntityBase entity = (EntityBase)obj;

							if (entity.mcaID == manager.worldProperties.playerSpouseID)
							{
								spouse = (EntityVillagerAdult)entity;
							}
						}
					}

					spouse.shouldDivorce = true;
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(spouse.entityId, "shouldDivorce", true));
				}
			}

			catch (Throwable e)
			{
				//The spouse wasn't found in the entities map for some reason. Just reset the player's spouse ID.
				manager.worldProperties.playerSpouseID = 0;
				manager.saveWorldProperties();
				entityVillager.notifyPlayer(player, Localization.getString("notify.divorce.spousemissing"));
			}

			close();
		}

		else if (button == divorceCoupleButton)
		{
			player.openGui(MCA.instance, MCA.instance.guiSpecialDivorceCoupleID, player.worldObj, (int)player.posX, (int)player.posY, (int)player.posZ);
		}

		else if (button == giveUpBabyButton)
		{
			manager.worldProperties.babyExists = false;
			manager.worldProperties.babyName = "";
			manager.worldProperties.babyReadyToGrow = false;
			manager.worldProperties.babyGender = "";
			manager.worldProperties.minutesBabyExisted = 0;

			entityVillager.notifyPlayer(player, Localization.getString("notify.baby.gaveup"));
			manager.saveWorldProperties();

			close();
		}

		else if (button == adoptBabyButton)
		{
			if (manager.worldProperties.babyExists)
			{
				entityVillager.notifyPlayer(player, Localization.getString("notify.baby.exists"));
			}

			else
			{
				manager.worldProperties.babyExists = true;
				manager.worldProperties.minutesBabyExisted = 0;
				manager.worldProperties.babyReadyToGrow = false;

				if (entityVillager.getRandomGender().equals("Male"))
				{
					manager.worldProperties.babyName = entityVillager.getRandomName("Male");
					entityVillager.say(Localization.getString(player, "priest.adopt.male"));

					player.inventory.addItemStackToInventory(new ItemStack(MCA.instance.itemBabyBoy, 1));
					PacketDispatcher.sendPacketToServer(PacketCreator.createAddItemPacket(MCA.instance.itemBabyBoy.itemID, player.entityId));
				}

				else
				{
					manager.worldProperties.babyName = entityVillager.getRandomName("Female");
					entityVillager.say(Localization.getString(player, "priest.adopt.female"));

					player.inventory.addItemStackToInventory(new ItemStack(MCA.instance.itemBabyGirl, 1));
					PacketDispatcher.sendPacketToServer(PacketCreator.createAddItemPacket(MCA.instance.itemBabyGirl.itemID, player.entityId));
				}

				manager.saveWorldProperties();
			}

			close();
		}
		
		else if (button == arrangedMarriageButton)
		{
			List<EntityVillagerAdult> nearbyVillagers = (List<EntityVillagerAdult>) Logic.getAllEntitiesOfTypeWithinDistanceOfEntity(entityVillager, EntityVillagerAdult.class, 30);
			
			String preferredGender = manager.worldProperties.playerGender.equals("Male") ? "Female" : "Male";
			EntityVillagerAdult villagerToMarry = null;
			
			for (EntityVillagerAdult adult : nearbyVillagers)
			{
				if (adult.gender.equals(preferredGender))
				{
					if (EntityBase.getBooleanWithProbability(30))
					{
						villagerToMarry = adult;
						break;
					}
				}
			}
			
			if (villagerToMarry == null)
			{
				player.addChatMessage(Localization.getString("notify.arrangedmarriage.failed"));
				return;
			}
			
			else
			{
				villagerToMarry.marriageToPlayerWasArranged = true;
				villagerToMarry.isSpouse = true;
				villagerToMarry.spousePlayerName = player.username;
				villagerToMarry.familyTree.addFamilyTreeEntry(player, EnumRelation.Spouse);

				player.triggerAchievement(MCA.instance.achievementGetMarried);

				manager.worldProperties.playerSpouseID = villagerToMarry.mcaID;
				manager.saveWorldProperties();
				
				//Reset AI in case of guard.
				villagerToMarry.addAI();
				
				PacketDispatcher.sendPacketToServer(PacketCreator.createFamilyTreePacket(villagerToMarry.entityId, villagerToMarry.familyTree));
				PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(villagerToMarry.entityId, "isSpouse", true));
				PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(villagerToMarry.entityId, "spousePlayerName", player.username));
				PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(villagerToMarry.entityId, "marriageToPlayerWasArranged", true));
				PacketDispatcher.sendPacketToServer(PacketCreator.createAchievementPacket(MCA.instance.achievementGetMarried, player.entityId));
				
				villagerToMarry.setPosition(player.posX, player.posY, player.posZ);
				PacketDispatcher.sendPacketToServer(PacketCreator.createPositionPacket(villagerToMarry, player.posX, player.posY, player.posZ));
				
				entityVillager.say(Localization.getString(player, villagerToMarry, "priest.arrangemarriage", false));
				close();
			}
		}
	}

	/**
	 * Handles an action performed in the miner's special GUI.
	 * 
	 * @param	button	The button that was pressed.
	 */
	private void actionPerformedMiner(GuiButton button) 
	{
		if (button == hireButton)
		{
			boolean playerHasGold = false;

			for (int i = 0; i < player.inventory.mainInventory.length; i++)
			{
				ItemStack stack = player.inventory.mainInventory[i];

				if (stack != null)
				{
					if (stack.getItem().itemID == Item.ingotGold.itemID)
					{
						if (stack.stackSize >= 3)
						{
							player.inventory.decrStackSize(i, 3);
							PacketDispatcher.sendPacketToServer(PacketCreator.createRemoveItemPacket(player.entityId, i, 3, 0));
							playerHasGold = true;
							break;
						}
					}
				}
			}

			if (playerHasGold)
			{
				//Set them to "hired".
				PlayerMemory memory = entityVillager.playerMemoryMap.get(player.username);
				memory.isHired = true;
				entityVillager.playerMemoryMap.put(player.username, memory);

				PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "playerMemoryMap", entityVillager.playerMemoryMap));
			}

			else
			{
				entityVillager.say(Localization.getString("miner.hire.refuse"));
			}

			close();
		}

		else if (button == dismissButton)
		{
			PlayerMemory memory = entityVillager.playerMemoryMap.get(player.username);
			memory.isHired = false;
			entityVillager.playerMemoryMap.put(player.username, memory);

			entityVillager.say(Localization.getString("miner.hire.dismiss"));
			entityVillager.isFollowing = false;
			entityVillager.isStaying = false;

			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isFollowing", false));
			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isStaying", false));
			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "playerMemoryMap", entityVillager.playerMemoryMap));
			close();
		}

		else if (button == miningButton)
		{
			if (entityVillager.isInChoreMode)
			{
				entityVillager.isInChoreMode = false;
				PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isInChoreMode", false));
				entityVillager.miningChore.endChore();
			}

			else
			{
				drawMiningGui();
			}
		}
	}

	/**
	 * Handles an action performed in the baker's special GUI.
	 * 
	 * @param	button	The button that was pressed.
	 */
	private void actionPerformedBaker(GuiButton button) 
	{
		if (button == aidButton)
		{
			if (entityVillager.aidCooldown != 0)
			{
				entityVillager.say(Localization.getString("baker.aid.refuse"));
			}

			else
			{
				if (entityVillager.getBooleanWithProbability(80))
				{
					Object[] giftInfo = null;
					giftInfo = DataStore.bakerAidIDs[entityVillager.worldObj.rand.nextInt(DataStore.bakerAidIDs.length)];
					int quantityGiven = entityVillager.worldObj.rand.nextInt(Integer.parseInt(giftInfo[2].toString())) + Integer.parseInt(giftInfo[1].toString());


					PacketDispatcher.sendPacketToServer(PacketCreator.createDropItemPacket(entityVillager.entityId, Integer.parseInt(giftInfo[0].toString()), quantityGiven));
					entityVillager.say(Localization.getString("baker.aid.accept"));
					entityVillager.aidCooldown = 12000;
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "aidCooldown", 12000));
				}

				else
				{
					entityVillager.say(Localization.getString("baker.aid.refuse"));
					entityVillager.aidCooldown = 12000;
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "aidCooldown", 12000));
				}
			}

			close();
		}
	}

	/**
	 * Handles an action performed in the guard's special GUI.
	 * 
	 * @param	button	The button that was pressed.
	 */
	private void actionPerformedGuard(GuiButton button) 
	{
		if (button == hireButton)
		{
			boolean playerHasGold = false;

			for (int i = 0; i < player.inventory.mainInventory.length; i++)
			{
				ItemStack stack = player.inventory.mainInventory[i];

				if (stack != null)
				{
					if (stack.getItem().itemID == Item.ingotGold.itemID)
					{
						if (stack.stackSize >= 3)
						{
							player.inventory.decrStackSize(i, 3);
							PacketDispatcher.sendPacketToServer(PacketCreator.createRemoveItemPacket(player.entityId, i, 3, 0));
							playerHasGold = true;
							break;
						}
					}
				}
			}

			if (playerHasGold)
			{
				//Set them to "hired".
				PlayerMemory memory = entityVillager.playerMemoryMap.get(player.username);
				memory.isHired = true;
				entityVillager.playerMemoryMap.put(player.username, memory);

				PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "playerMemoryMap", entityVillager.playerMemoryMap));
			}

			else
			{
				entityVillager.say(Localization.getString("guard.hire.refuse"));
			}

			close();
		}

		else if (button == dismissButton)
		{
			entityVillager.say(Localization.getString("guard.hire.dismiss"));
			entityVillager.isFollowing = false;
			entityVillager.isStaying = false;

			PlayerMemory memory = entityVillager.playerMemoryMap.get(player.username);
			memory.isHired = false;
			entityVillager.playerMemoryMap.put(player.username, memory);

			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isFollowing", false));
			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isStaying", false));
			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "playerMemoryMap", entityVillager.playerMemoryMap));
			close();
		}

		else if (button == combatButton)
		{
			drawCombatGui();
		}
	}

	/**
	 * Handles an action performed in the butcher's special GUI.
	 * 
	 * @param	button	The button that was pressed.
	 */
	private void actionPerformedButcher(GuiButton button) 
	{
		if (button == aidButton)
		{
			if (entityVillager.aidCooldown != 0)
			{
				entityVillager.say(Localization.getString("butcher.aid.refuse"));
			}

			else
			{
				if (entityVillager.getBooleanWithProbability(80))
				{
					Object[] giftInfo = null;
					giftInfo = DataStore.butcherAidIDs[entityVillager.worldObj.rand.nextInt(DataStore.butcherAidIDs.length)];
					int quantityGiven = entityVillager.worldObj.rand.nextInt(Integer.parseInt(giftInfo[2].toString())) + Integer.parseInt(giftInfo[1].toString());

					PacketDispatcher.sendPacketToServer(PacketCreator.createDropItemPacket(entityVillager.entityId, Integer.parseInt(giftInfo[0].toString()), quantityGiven));
					entityVillager.say(Localization.getString("butcher.aid.accept"));

					entityVillager.aidCooldown = 12000;
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "aidCooldown", 12000));
				}

				else
				{
					entityVillager.say(Localization.getString("butcher.aid.refuse"));
					entityVillager.aidCooldown = 12000;
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "aidCooldown", 12000));
				}
			}

			close();
		}
	}

	/**
	 * Handles an action performed in the smith's special GUI.
	 * 
	 * @param	button	The button that was pressed.
	 */
	private void actionPerformedSmith(GuiButton button) 
	{
		if (button == aidButton)
		{
			if (entityVillager.itemIdRequiredForSale == 0)
			{
				List<Item> possibleItems = new ArrayList<Item>();
				possibleItems.add(Item.diamond);
				possibleItems.add(Item.ingotGold);
				possibleItems.add(Item.emerald);
				possibleItems.add(Item.ingotIron);
				possibleItems.add(Item.coal);

				entityVillager.itemIdRequiredForSale = possibleItems.get(entityVillager.worldObj.rand.nextInt(possibleItems.size())).itemID;

				if (entityVillager.itemIdRequiredForSale == Item.diamond.itemID)   entityVillager.amountRequiredForSale = entityVillager.worldObj.rand.nextInt(2) + 1;
				if (entityVillager.itemIdRequiredForSale == Item.ingotGold.itemID) entityVillager.amountRequiredForSale = entityVillager.worldObj.rand.nextInt(6) + 1;
				if (entityVillager.itemIdRequiredForSale == Item.emerald.itemID)   entityVillager.amountRequiredForSale = entityVillager.worldObj.rand.nextInt(2) + 1;
				if (entityVillager.itemIdRequiredForSale == Item.ingotIron.itemID) entityVillager.amountRequiredForSale = entityVillager.worldObj.rand.nextInt(12) + 1;
				if (entityVillager.itemIdRequiredForSale == Item.coal.itemID)      entityVillager.amountRequiredForSale = entityVillager.worldObj.rand.nextInt(20) + 1;
			}

			entityVillager.say(Localization.getString("smith.aid.prompt"));
			entityVillager.isInAnvilGiftMode = true;

			PlayerMemory memory = entityVillager.playerMemoryMap.get(player.username);
			memory.isInGiftMode = true;
			entityVillager.playerMemoryMap.put(player.username, memory);

			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "itemIdRequiredForSale", entityVillager.itemIdRequiredForSale));
			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "amountRequiredForSale", entityVillager.amountRequiredForSale));
			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isInAnvilGiftMode", true));
			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "playerMemoryMap", entityVillager.playerMemoryMap));
		}

		close();
	}

	/**
	 * Handles an action performed in the farmer's special GUI.
	 * 
	 * @param	button	The button that was pressed.
	 */
	private void actionPerformedFarmer(GuiButton button)
	{
		if (button == aidButton)
		{
			if (entityVillager.aidCooldown != 0)
			{
				entityVillager.say(Localization.getString("farmer.aid.refuse"));
			}

			else
			{
				if (entityVillager.getBooleanWithProbability(80))
				{
					Object[] giftInfo = null;
					giftInfo = DataStore.farmerAidIDs[entityVillager.worldObj.rand.nextInt(DataStore.farmerAidIDs.length)];
					int quantityGiven = entityVillager.worldObj.rand.nextInt(Integer.parseInt(giftInfo[2].toString())) + Integer.parseInt(giftInfo[1].toString());


					PacketDispatcher.sendPacketToServer(PacketCreator.createDropItemPacket(entityVillager.entityId, Integer.parseInt(giftInfo[0].toString()), quantityGiven));
					entityVillager.say(Localization.getString("farmer.aid.accept"));
					entityVillager.aidCooldown = 12000;
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "aidCooldown", 12000));
				}

				else
				{
					entityVillager.say(Localization.getString("farmer.aid.refuse"));
					entityVillager.aidCooldown = 12000;
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "aidCooldown", 12000));
				}
			}

			close();
		}
	}

	/**
	 * Handles an action performed in the librarian's special GUI.
	 * 
	 * @param	button	The button that was pressed.
	 */
	private void actionPerformedLibrarian(GuiButton button)
	{
		if (button == openSetupButton)
		{
			mc.displayGuiScreen(null);
			player.openGui(MCA.instance, MCA.instance.guiSetupID, player.worldObj, (int)entityVillager.posX, (int)entityVillager.posY, (int)entityVillager.posZ);
		}
	}

	/**
	 * Handles an action performed in the mining special GUI.
	 * 
	 * @param	button	The button that was pressed.
	 */
	private void actionPerformedMining(GuiButton button)
	{
		if (button == backButton)
		{
			drawMinerSpecialGui();
		}

		else if (button == mineMethodButton)
		{
			if (mineMethod == 1)
			{
				mineMethod = 0;
			}

			else
			{
				mineMethod = 1;
			}

			drawMiningGui();
		}

		else if (button == mineDirectionButton)
		{
			if (mineDirection == 3)
			{
				mineDirection = 0;
			}

			else
			{
				mineDirection++;
			}

			drawMiningGui();
		}

		else if (button == mineDistanceButton)
		{
			if (mineDistance == 100)
			{
				mineDistance = 5;
			}

			else
			{
				mineDistance += 5;
			}

			drawMiningGui();
		}

		else if (button == mineFindButton)
		{
			if (mineOre == 6)
			{
				mineOre = 0;
			}

			else
			{
				mineOre++;
			}

			drawMiningGui();
		}

		else if (button == mineStartButton)
		{
			entityVillager.miningChore = new ChoreMining(entityVillager, mineMethod, mineDirection, mineOre, mineDistance);
			entityVillager.isInChoreMode = true;
			entityVillager.currentChore = entityVillager.miningChore.getChoreName();

			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isInChoreMode", true));
			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "currentChore", "Mining"));
			PacketDispatcher.sendPacketToServer(PacketCreator.createChorePacket(entityVillager.entityId, entityVillager.miningChore));
			close();
		}
	}

	/**
	 * Handles an action performed in the combat GUI.
	 * 
	 * @param 	button	The button that was pressed.
	 */
	private void actionPerformedCombat(GuiButton button)
	{
		if (button == backButton)
		{
			drawGuardSpecialGui();
			return;
		}

		else if (button == combatMethodButton)
		{
			if (entityVillager.combatChore.useMelee && entityVillager.combatChore.useRange)
			{
				entityVillager.combatChore.useMelee = false;
				entityVillager.combatChore.useRange = false;
			}

			else if (entityVillager.combatChore.useMelee)
			{
				entityVillager.combatChore.useMelee = false;
				entityVillager.combatChore.useRange = true;
			}

			else if (entityVillager.combatChore.useRange)
			{
				entityVillager.combatChore.useMelee = true;
				entityVillager.combatChore.useRange = true;
			}

			else
			{
				entityVillager.combatChore.useMelee = true;
				entityVillager.combatChore.useRange = false;
			}
		}

		else if (button == combatAttackPigsButton)
		{
			entityVillager.combatChore.attackPigs = !entityVillager.combatChore.attackPigs;
		}

		else if (button == combatAttackSheepButton)
		{
			entityVillager.combatChore.attackSheep = !entityVillager.combatChore.attackSheep;
		}

		else if (button == combatAttackCowsButton)
		{
			entityVillager.combatChore.attackCows = !entityVillager.combatChore.attackCows;
		}

		else if (button == combatAttackChickensButton)
		{
			entityVillager.combatChore.attackChickens = !entityVillager.combatChore.attackChickens;
		}

		else if (button == combatAttackSpidersButton)
		{
			entityVillager.combatChore.attackSpiders = !entityVillager.combatChore.attackSpiders;
		}

		else if (button == combatAttackZombiesButton)
		{
			entityVillager.combatChore.attackZombies = !entityVillager.combatChore.attackZombies;
		}

		else if (button == combatAttackSkeletonsButton)
		{
			entityVillager.combatChore.attackSkeletons = !entityVillager.combatChore.attackSkeletons;
		}

		else if (button == combatAttackCreepersButton)
		{
			entityVillager.combatChore.attackCreepers = !entityVillager.combatChore.attackCreepers;
		}

		else if (button == combatAttackEndermenButton)
		{
			entityVillager.combatChore.attackEndermen = !entityVillager.combatChore.attackEndermen;
		}

		else if (button == combatAttackUnknownButton)
		{
			entityVillager.combatChore.attackUnknown = !entityVillager.combatChore.attackUnknown;
		}

		PacketDispatcher.sendPacketToServer(PacketCreator.createChorePacket(entityVillager.entityId, entityVillager.combatChore));
		drawCombatGui();
	}

	/**
	 * Handles an action performed in the Monarch GUI.
	 * 
	 * @param 	button	The button that was pressed.
	 */
	private void actionPerformedMonarch(GuiButton button)
	{
		if (button == backButton)
		{
			drawInteractionGui();
		}

		else if (button == executeButton)
		{
			boolean hasSword = false;

			for (ItemStack itemStack : player.inventory.mainInventory)
			{
				if (itemStack != null)
				{
					if (itemStack.getItem() instanceof ItemSword)
					{
						hasSword = true;
						break;
					}
				}
			}

			if (hasSword)
			{
				if (!entityVillager.isSpouse)
				{
					entityVillager.hasBeenExecuted = true;

					//This will modify all surrounding villagers, too.
					entityVillager.modifyHearts(player, -30);

					//Update stats and check for achievement.
					WorldPropertiesManager manager = MCA.instance.playerWorldManagerMap.get(player.username);
					manager.worldProperties.stat_villagersExecuted++;
					manager.saveWorldProperties();
					
					player.triggerAchievement(MCA.instance.achievementExecuteVillager);
					PacketDispatcher.sendPacketToServer(PacketCreator.createAchievementPacket(MCA.instance.achievementExecuteVillager, player.entityId));
					
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "hasBeenExecuted", entityVillager.hasBeenExecuted));
					close();
				}

				else
				{
					player.addChatMessage(Localization.getString("monarch.execute.failure.playerspouse"));
					close();
				}
			}

			else
			{
				player.addChatMessage(Localization.getString("monarch.execute.failure.noweapon"));
				close();
			}
		}

		else if (button == demandGiftButton)
		{
			PlayerMemory memory = entityVillager.playerMemoryMap.get(player.username);

			//Increase gifts demanded.
			memory.monarchGiftsDemanded++;

			//Don't want to set ticks back to the maximum when they're in the process of counting down. Only reset them when
			//they're already zero.
			if (memory.monarchResetTicks <= 0)
			{
				memory.monarchResetTicks = 48000;
			}

			//More than two is too many.
			if (memory.monarchGiftsDemanded > 2)
			{
				//Modifying hearts affects everyone in the area.
				entityVillager.modifyHearts(player, -(5 * memory.monarchGiftsDemanded));

				//There is a chance of refusing, and continue to refuse after doing so.
				if (EntityBase.getBooleanWithProbability(5 * memory.monarchGiftsDemanded) || memory.hasRefusedDemands)
				{
					memory.hasRefusedDemands = true;
					entityVillager.say(Localization.getString(player, "monarch.demandgift.dictator"));

					//Update, send to server, and stop here.
					entityVillager.playerMemoryMap.put(player.username, memory);
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "playerMemoryMap", entityVillager.playerMemoryMap));

					close();
					return;
				}

				else
				{
					entityVillager.say(Localization.getString(player, "monarch.demandgift.toomany"));
				}
			}

			//Accept when less than 2.
			else
			{
				entityVillager.say(Localization.getString(player, "monarch.demandgift.accept"));
			}

			entityVillager.playerMemoryMap.put(player.username, memory);
			ItemStack giftStack = Logic.getGiftStackFromRelationship(player, entityVillager);

			PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "playerMemoryMap", entityVillager.playerMemoryMap));
			PacketDispatcher.sendPacketToServer(PacketCreator.createDropItemPacket(entityVillager.entityId, giftStack.itemID, giftStack.stackSize));
			close();
		}

		else if (button == makePeasantButton)
		{
			if (!entityVillager.isPeasant)
			{
				if (entityVillager.isSpouse)
				{
					player.addChatMessage(Localization.getString("monarch.makepeasant.failure.playerspouse"));
					close();
				}

				else
				{
					entityVillager.isPeasant = true;
					entityVillager.monarchPlayerName = player.username;

					//Update stats and check for achievement.
					WorldPropertiesManager manager = MCA.instance.playerWorldManagerMap.get(player.username);
					manager.worldProperties.stat_villagersMadePeasants++;
					manager.saveWorldProperties();
					
					if (manager.worldProperties.stat_villagersMadePeasants >= 20)
					{
						player.triggerAchievement(MCA.instance.achievementPeasantArmy);
						PacketDispatcher.sendPacketToServer(PacketCreator.createAchievementPacket(MCA.instance.achievementPeasantArmy, player.entityId));
					}
					
					player.addChatMessage(Localization.getString("monarch.makepeasant.success"));

					player.triggerAchievement(MCA.instance.achievementMakePeasant);
					PacketDispatcher.sendPacketToServer(PacketCreator.createAchievementPacket(MCA.instance.achievementMakePeasant, player.entityId));
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isPeasant", entityVillager.isPeasant));
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "monarchPlayerName", entityVillager.monarchPlayerName));
					close();
				}
			}

			else
			{
				player.addChatMessage(Localization.getString("monarch.makepeasant.failure.alreadypeasant"));
				close();
			}
		}

		else if (button == makeKnightButton)
		{
			if (!entityVillager.isKnight)
			{
				if (entityVillager.isSpouse)
				{
					player.addChatMessage(Localization.getString("monarch.makeknight.failure.playerspouse"));
					close();
				}

				else
				{
					entityVillager.isKnight = true;
					entityVillager.monarchPlayerName = player.username;
					
					//Update stats and check for achievement.
					WorldPropertiesManager manager = MCA.instance.playerWorldManagerMap.get(player.username);
					manager.worldProperties.stat_guardsMadeKnights++;
					manager.saveWorldProperties();
					
					if (manager.worldProperties.stat_guardsMadeKnights >= 20)
					{
						player.triggerAchievement(MCA.instance.achievementMakeKnight);
						PacketDispatcher.sendPacketToServer(PacketCreator.createAchievementPacket(MCA.instance.achievementKnightArmy, player.entityId));
					}
					
					player.addChatMessage(Localization.getString("monarch.makeknight.success"));

					player.triggerAchievement(MCA.instance.achievementMakeKnight);
					PacketDispatcher.sendPacketToServer(PacketCreator.createAchievementPacket(MCA.instance.achievementMakeKnight, player.entityId));
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "isKnight", entityVillager.isKnight));
					PacketDispatcher.sendPacketToServer(PacketCreator.createFieldValuePacket(entityVillager.entityId, "monarchPlayerName", entityVillager.monarchPlayerName));
					close();
				}
			}

			else
			{
				player.addChatMessage(Localization.getString("monarch.makeknight.failure.alreadyknight"));
				close();
			}
		}
	}
}
