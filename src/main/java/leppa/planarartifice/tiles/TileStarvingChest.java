package leppa.planarartifice.tiles;

import com.google.common.base.Predicate;
import leppa.planarartifice.blocks.BlockStarvingChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import thaumcraft.api.ThaumcraftInvHelper;
import thaumcraft.common.tiles.devices.TileHungryChest;

import java.util.List;

public class TileStarvingChest extends TileHungryChest {
    public int upgrades;
    public TileStarvingChest() { this(1); }
    public TileStarvingChest(int upgrades) {
        super();
        this.upgrades = upgrades;
    }

    // stole from openblocks -p
    @Override
    public void update() {
        super.update();
        List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.add(-upgrades, 0, -upgrades), pos.add(1+upgrades, 1+upgrades, 1+upgrades)), filterEntity);
        for (Entity e : entities) {
            double x = (0.5 + pos.getX()) - e.posX;
            double y = (0.5 + pos.getY()) - e.posY;
            double z = (0.5 + pos.getZ()) - e.posZ;
            double dist = Math.sqrt(x * x + y * y + z * z);
            if (dist > 1.1) {
                double q = dist * (dist - 1) * (dist - 1);
                e.motionX += x / (q * 11.2);
                e.motionY += y / (q * 45);
                e.motionZ += z / (q * 11.2);
            }
        }
    }

    private final Predicate<Entity> filterEntity = entity -> {
      if (!entity.isDead && entity instanceof EntityItem) return ThaumcraftInvHelper.hasRoomForSome(world, pos, EnumFacing.UP, ((EntityItem) entity).getItem());
      return false;
    };

    @Override
    public void closeInventory(EntityPlayer player) {
        if (!player.isSpectator() && this.getBlockType() instanceof BlockStarvingChest) {
            --this.numPlayersUsing;
            this.world.addBlockEvent(this.pos, this.getBlockType(), 1, this.numPlayersUsing);
            this.world.notifyNeighborsOfStateChange(this.pos, this.getBlockType(), true);
            this.world.notifyNeighborsOfStateChange(this.pos.down(), this.getBlockType(), true);
        }
    }
}
