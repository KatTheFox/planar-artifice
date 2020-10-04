package leppa.planarartifice.tiles;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import thaumcraft.api.aura.AuraHelper;
import thaumcraft.api.items.ItemsTC;

import javax.annotation.Nullable;

public class TileFluxScrubber extends TileEntity implements ITickable {

	public ItemStackHandler inventory = new ItemStackHandler(1);
	private int currentSaltTime = 0;
	public static final int maxSaltTime = 300;

	@Override
	public void update() {
		if(currentSaltTime > 0) {
			if(AuraHelper.drainVis(world, getPos(), 1, true) > 0 && AuraHelper.drainFlux(world, getPos(), 1, true) > 0) {
				AuraHelper.drainFlux(world, getPos(), 1, false);
				AuraHelper.drainVis(world, getPos(), 1, false);

				if(world.rand.nextBoolean()) {
					if(AuraHelper.drainFlux(world, getPos(), 1, true) > 0) {
						AuraHelper.drainFlux(world, getPos(), 1, false);
					}
				}

			}

			currentSaltTime--;
			return;
		}

		if(inventory.getStackInSlot(0).getItem() == ItemsTC.salisMundus) {
			inventory.extractItem(0, 1, false);
			currentSaltTime = maxSaltTime;
		}

	}

	public int getSaltTimeScaled() {
		return (int) (currentSaltTime / 12.5);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		compound.setTag("inventory", inventory.serializeNBT());
		compound.setInteger("currentSaltTime", currentSaltTime);
		return super.writeToNBT(compound);
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		inventory.deserializeNBT(compound.getCompoundTag("inventory"));
		currentSaltTime = compound.getInteger("currentSaltTime");
		super.readFromNBT(compound);
	}

	@Override
	public boolean hasCapability(Capability<?> cap, @Nullable EnumFacing facing) {
		return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? true : super.hasCapability(cap, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> cap, @Nullable EnumFacing facing) {
		if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) return (T) inventory;
		return super.getCapability(cap, facing);
	}

}
