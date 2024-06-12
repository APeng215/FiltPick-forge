package net.apeng.filtpick.mixin;


import com.mojang.authlib.GameProfile;
import net.apeng.filtpick.gui.screen.FiltPickScreen;
import net.apeng.filtpick.mixinduck.FiltListContainer;
import net.apeng.filtpick.gui.util.ExtendedMenuProvider;
import net.apeng.filtpick.property.FiltListPropertyDelegate;
import net.apeng.filtpick.util.SlotsContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



@Mixin(ServerPlayer.class)
public abstract class ServerPlayerEntityMixin extends Player implements FiltListContainer {

    @Unique
    private SimpleContainer filtList = new SlotsContainer(FiltListContainer.CAPACITY);

    @Unique
    private FiltListPropertyDelegate filtListPropertyDelegate = new FiltListPropertyDelegate();

    @Shadow public abstract void closeContainer();

    @Shadow public abstract void doCloseContainer();

    public ServerPlayerEntityMixin(Level world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    public void readFiltPickInventoryInfoFromNbt(CompoundTag nbt, CallbackInfo callbackInfo) {
        readFiltList(nbt);
        readPropertyDelegate(nbt);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    public void writeFiltPickInventoryInfoToNbt(CompoundTag nbt, CallbackInfo callbackInfo) {
        writeFiltList(nbt);
        writePropertyDelegate(nbt);
    }

    // To keep list after death
    @Inject(method = "restoreFrom", at = @At("TAIL"))
    public void copyFilePickInventory(ServerPlayer oldPlayer, boolean alive, CallbackInfo ci) {
        copyFiltList((FiltListContainer) oldPlayer);
        copyPropertyDelegate((FiltListContainer) oldPlayer);
    }

    @Redirect(method = "openMenu(Lnet/minecraft/world/MenuProvider;)Ljava/util/OptionalInt;", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;closeContainer()V"))
    public void shouldCloseCurrentScreenHook(ServerPlayer instance, MenuProvider menuProvider) {
        if (menuProvider instanceof ExtendedMenuProvider extendedMenuProvider) {
            if (extendedMenuProvider.shouldClose()) {
                this.closeContainer();
            } else {
                this.doCloseContainer();
            }
        } else {
            this.closeContainer();
        }
    }

    @Unique
    private void readFiltList(CompoundTag nbt) {
        this.filtList.fromTag(nbt.getList("FiltList", 10));
    }

    @Unique
    private void readPropertyDelegate(CompoundTag nbt) {
        filtListPropertyDelegate.set(FiltPickScreen.WHITELIST_MODE_BUTTON_ID, nbt.getInt("isWhiteListModeOn"));
        filtListPropertyDelegate.set(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID, nbt.getInt("isDestructionModeOn"));
    }


    /**
     * {"FiltList": [itemCompound#1, itemCompound#2...], ...}
     * @param nbt
     */
    @Unique
    private void writeFiltList(CompoundTag nbt) {
        nbt.put("FiltList", this.filtList.createTag());
    }

    @Unique
    private void writePropertyDelegate(CompoundTag nbt) {
        nbt.putInt("isWhiteListModeOn", filtListPropertyDelegate.get(FiltPickScreen.WHITELIST_MODE_BUTTON_ID));
        nbt.putInt("isDestructionModeOn", filtListPropertyDelegate.get(FiltPickScreen.DESTRUCTION_MODE_BUTTON_ID));
    }

    @Unique
    private void copyPropertyDelegate(FiltListContainer oldPlayer) {
        this.filtListPropertyDelegate = oldPlayer.getFiltListPropertyDelegate();
    }

    @Unique
    private void copyFiltList(FiltListContainer oldPlayer) {
        this.filtList = oldPlayer.getFiltList();
    }

    @Override
    public SimpleContainer getFiltList() {
        return this.filtList;
    }

    @Override
    public FiltListPropertyDelegate getFiltListPropertyDelegate() {
        return this.filtListPropertyDelegate;
    }

    @Override
    public void resetFiltListAndProperties() {
        getFiltList().clearContent();
        getFiltListPropertyDelegate().reset();
    }


}
