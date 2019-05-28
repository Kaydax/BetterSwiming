package baguchan.betterswiming;

import baguchan.betterswiming.client.RenderPlayerSwiming;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelPlayer;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mod(modid = BetterSwimingCore.MODID, name = BetterSwimingCore.NAME, version = BetterSwimingCore.VERSION, useMetadata = true,dependencies = "required:forge@[14.23.5.2811,);")
public class BetterSwimingCore {
    public static final String MODID = "betterswiming";
    public static final String NAME = "BetterSwiming";
    public static final String VERSION = "1.0.3";

    @Mod.Metadata
    public static ModMetadata metadata;


    public static Method setSize = ObfuscationReflectionHelper.findMethod(Entity.class, "func_70105_a", void.class, float.class, float.class);

    @EventHandler
    public void construct(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }


    @SubscribeEvent
    public void adjustSize(TickEvent.PlayerTickEvent event) {


        EntityPlayer player = event.player;



        if (player.isInWater() && player.isSprinting()||this.isOpaqueBlock(player)) {
            player.height = (float) 0.6;

            player.width = (float) 0.6;

            player.eyeHeight = (float) 0.45;


            try
            {
                setSize.invoke(player, player.width, player.height);
            }
            catch (IllegalAccessException e)
            {
                e.printStackTrace();
            }
            catch (IllegalArgumentException e)
            {
                e.printStackTrace();
            }
            catch (InvocationTargetException e)
            {
                e.printStackTrace();
            }


            AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();

            axisalignedbb = new AxisAlignedBB(player.posX - player.width / 2.0D, axisalignedbb.minY,

                    player.posZ - player.width / 2.0D, player.posX + player.width / 2.0D,

                    axisalignedbb.minY + player.height, player.posZ + player.width / 2.0D);

            player.setEntityBoundingBox(axisalignedbb);


        } else {

            player.eyeHeight = player.getDefaultEyeHeight();
        }
    }

    public boolean isOpaqueBlock(EntityPlayer player)
    {
        if (player.noClip)
        {
            return false;
        }
        else
        {
            BlockPos.PooledMutableBlockPos blockpos$pooledmutableblockpos = BlockPos.PooledMutableBlockPos.retain();

            for (int i = 0; i < 8; ++i)
            {
                int j = MathHelper.floor(player.posY + (double)(1.0F));
                int k = MathHelper.floor(player.posX + (double)(((float)((i >> 1) % 2) - 0.5F) * player.width * 0.8F));
                int l = MathHelper.floor(player.posZ + (double)(((float)((i >> 2) % 2) - 0.5F) * player.width * 0.8F));

                if (blockpos$pooledmutableblockpos.getX() != k || blockpos$pooledmutableblockpos.getY() != j || blockpos$pooledmutableblockpos.getZ() != l)
                {
                    blockpos$pooledmutableblockpos.setPos(k, j, l);

                    if (player.world.getBlockState(blockpos$pooledmutableblockpos).causesSuffocation())
                    {
                        blockpos$pooledmutableblockpos.release();
                        return true;
                    }
                }
            }

            blockpos$pooledmutableblockpos.release();
            return false;
        }
    }


    @SubscribeEvent
    public void onLivingPlayer(LivingEvent.LivingUpdateEvent event) {
        EntityLivingBase entityLivingBase = event.getEntityLiving();

        if (entityLivingBase instanceof EntityPlayer) {

            EntityPlayer player = (EntityPlayer) event.getEntity();
            if (player.isInWater() && player.isSprinting()) {

                if (player.motionX < -0.4D) {
                    player.motionX = -0.39F;
                }
                if (player.motionX > 0.4D) {
                    player.motionX = 0.39F;
                }

                if (player.motionY < -0.4D) {
                    player.motionY = -0.39F;
                }
                if (player.motionY > 0.4D) {
                    player.motionY = 0.39F;
                }
                if (player.motionZ < -0.4D) {
                    player.motionZ = -0.39F;
                }
                if (player.motionZ > 0.4D) {
                    player.motionZ = 0.39F;
                }

                double d3 = player.getLookVec().y;
                double d4 = d3 < -0.2D ? 0.085D : 0.06D;

                if (d3 <= 0.0D || player.world.getBlockState(new BlockPos(player.posX, player.posY + 1.0D - 0.64D, player.posZ)).getMaterial() == Material.WATER) {
                    player.motionY += (d3 - player.motionY) * d4;

                }
                double d6 = player.posY;

                player.motionY += 0.018D;


                player.motionX *= 1.005F;
                player.motionZ *= 1.005F;

                player.move(MoverType.SELF, player.motionX, player.motionY, player.motionZ);
            }
        }

    }


    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public void onLivingRender(RenderPlayerEvent.Pre event) {


        World world = Minecraft.getMinecraft().world;

        EntityPlayer player = event.getEntityPlayer();


        ModelPlayer model = event.getRenderer().getMainModel();
        ResourceLocation skinLoc = DefaultPlayerSkin.getDefaultSkin(player.getPersistentID());
        boolean type = false;


        if (player.isInWater() && player.isSprinting()||this.isOpaqueBlock(player)) {

            event.setCanceled(true);

            if (Minecraft.getMinecraft().getRenderViewEntity() instanceof AbstractClientPlayer) {

                AbstractClientPlayer client = ((AbstractClientPlayer) Minecraft.getMinecraft().getRenderViewEntity());
                type = client.getSkinType().equals("slim");

            }


            RenderPlayerSwiming sp = new RenderPlayerSwiming(event.getRenderer().getRenderManager(), type);

            sp.doRender(((AbstractClientPlayer) event.getEntity()), event.getX(), event.getY(), event.getZ(),
                    ((AbstractClientPlayer) event.getEntity()).rotationYaw, event.getPartialRenderTick());

        }

    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        loadMeta();
    }

    private void loadMeta() {

        metadata.modId = MODID;

        metadata.name = NAME;
        metadata.version = VERSION;

        metadata.description = ("this mod is make swiming better(like 1.13)!");
        metadata.credits = ("");
        metadata.logoFile = ("");

        metadata.url = ("");

        metadata.autogenerated = false;
    }
}
