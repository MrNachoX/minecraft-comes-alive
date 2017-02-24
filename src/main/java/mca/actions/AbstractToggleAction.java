package mca.actions;

import java.util.UUID;

import mca.entity.EntityVillagerMCA;
import mca.enums.EnumPersonality;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.text.TextComponentString;
import radixcore.modules.RadixLogic;

/**
 * Defines an AI that can be "toggled" on or off. It does not run consistently.
 */
public abstract class AbstractToggleAction extends AbstractAction
{
	protected static final DataParameter<Boolean> IS_AI_ACTIVE = EntityDataManager.<Boolean>createKey(EntityVillagerMCA.class, DataSerializers.BOOLEAN);
	
	/** The UUID of the player that triggered this AI. */
	protected UUID assigningPlayer = new UUID(0,0);
	
	public AbstractToggleAction(EntityVillagerMCA actor)
	{
		super(actor);
	}
	
	/** Sets this AI as active and begins calling the update methods. */
	public final void setIsActive(boolean value)
	{
		actor.getDataManager().set(IS_AI_ACTIVE, value);
	}
	
	/** @returns True if this AI is currently running. */
	public final boolean getIsActive()
	{
		return actor.getDataManager().get(IS_AI_ACTIVE);
	}
	
	/** @returns The user-friendly name of this AI. Displays above the actor's head. */
	protected abstract String getName();
	
	/** @returns The player who triggered this AI. Looks up the player by UUID. */
	public final EntityPlayer getAssigningPlayer()
	{
		return actor.world.getPlayerEntityByUUID(assigningPlayer);
	}
	
	/** Adds a chat message to the assigning player's chat log. */
	public final void notifyAssigningPlayer(String message)
	{
		final EntityPlayer player = getAssigningPlayer();
		
		if (player != null)
		{
			player.sendMessage(new TextComponentString(message));
		}
	}
	
	/** Handles duplicating stacks added to the inventory, or stacks ignored due to personality. */
	protected final boolean addItemStackToInventory(ItemStack stack)
	{
		if (actor.getPersonality() == EnumPersonality.CURIOUS && RadixLogic.getBooleanWithProbability(10))
		{
			actor.getVillagerInventory().addItem(stack);
			return actor.getVillagerInventory().addItem(stack.copy()) == null;
		}
		
		else if (actor.getPersonality() == EnumPersonality.GREEDY && RadixLogic.getBooleanWithProbability(10))
		{
			return false;
		}
		
		else if (actor.getPersonality() != EnumPersonality.GREEDY)
		{
			return actor.getVillagerInventory().addItem(stack) == null;
		}
		
		return false;
	}
	
	protected void registerDataParameters()
	{
		try
		{
			actor.getDataManager().get(IS_AI_ACTIVE);
		}
		
		catch (NullPointerException e) //When not registered
		{
			actor.getDataManager().register(IS_AI_ACTIVE, false);
		}
	}
}