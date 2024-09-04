package gregtech.common.covers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;

import com.gtnewhorizons.modularui.api.screen.ModularWindow;
import com.gtnewhorizons.modularui.common.widget.TextWidget;

import gregtech.api.gui.modularui.CoverUIBuildContext;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.ICoverable;
import gregtech.api.interfaces.tileentity.IMachineProgress;
import gregtech.api.metatileentity.BaseMetaPipeEntity;
import gregtech.api.util.CoverBehavior;
import gregtech.api.util.GTUtility;
import gregtech.api.util.ISerializableObject;
import gregtech.common.gui.modularui.widget.CoverDataControllerWidget;
import gregtech.common.gui.modularui.widget.CoverDataFollowerToggleButtonWidget;

public class CoverShutter extends CoverBehavior {

    public CoverShutter(ITexture coverTexture) {
        super(coverTexture);
    }

    @Override
    public boolean isRedstoneSensitive(ForgeDirection side, int aCoverID, int aCoverVariable, ICoverable aTileEntity,
        long aTimer) {
        return false;
    }

    @Override
    public int doCoverThings(ForgeDirection side, byte aInputRedstone, int aCoverID, int aCoverVariable,
        ICoverable aTileEntity, long aTimer) {
        return aCoverVariable;
    }

    @Override
    public int onCoverScrewdriverclick(ForgeDirection side, int aCoverID, int aCoverVariable, ICoverable aTileEntity,
        EntityPlayer aPlayer, float aX, float aY, float aZ) {
        aCoverVariable = (aCoverVariable + (aPlayer.isSneaking() ? -1 : 1)) % 4;
        if (aCoverVariable < 0) {
            aCoverVariable = 3;
        }
        switch (aCoverVariable) {
            case 0 -> GTUtility.sendChatToPlayer(aPlayer, GTUtility.trans("082", "Open if work enabled"));
            case 1 -> GTUtility.sendChatToPlayer(aPlayer, GTUtility.trans("083", "Open if work disabled"));
            case 2 -> GTUtility.sendChatToPlayer(aPlayer, GTUtility.trans("084", "Only Output allowed"));
            case 3 -> GTUtility.sendChatToPlayer(aPlayer, GTUtility.trans("085", "Only Input allowed"));
        }
        if (aTileEntity instanceof BaseMetaPipeEntity) {
            ((BaseMetaPipeEntity) aTileEntity).reloadLocks();
        }
        return aCoverVariable;
    }

    @Override
    public boolean letsRedstoneGoIn(ForgeDirection side, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return aCoverVariable >= 2 ? aCoverVariable == 3
            : !(aTileEntity instanceof IMachineProgress)
                || (((IMachineProgress) aTileEntity).isAllowedToWork() == (aCoverVariable % 2 == 0));
    }

    @Override
    public boolean letsRedstoneGoOut(ForgeDirection side, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return aCoverVariable >= 2 ? aCoverVariable == 2
            : !(aTileEntity instanceof IMachineProgress)
                || (((IMachineProgress) aTileEntity).isAllowedToWork() == (aCoverVariable % 2 == 0));
    }

    @Override
    public boolean letsEnergyIn(ForgeDirection side, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return aCoverVariable >= 2 ? aCoverVariable == 3
            : !(aTileEntity instanceof IMachineProgress)
                || (((IMachineProgress) aTileEntity).isAllowedToWork() == (aCoverVariable % 2 == 0));
    }

    @Override
    public boolean letsEnergyOut(ForgeDirection side, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return aCoverVariable >= 2 ? aCoverVariable == 2
            : !(aTileEntity instanceof IMachineProgress)
                || ((IMachineProgress) aTileEntity).isAllowedToWork() == (aCoverVariable % 2 == 0);
    }

    @Override
    public boolean letsFluidIn(ForgeDirection side, int aCoverID, int aCoverVariable, Fluid aFluid,
        ICoverable aTileEntity) {
        return aCoverVariable >= 2 ? aCoverVariable == 3
            : !(aTileEntity instanceof IMachineProgress)
                || ((IMachineProgress) aTileEntity).isAllowedToWork() == (aCoverVariable % 2 == 0);
    }

    @Override
    public boolean letsFluidOut(ForgeDirection side, int aCoverID, int aCoverVariable, Fluid aFluid,
        ICoverable aTileEntity) {
        return aCoverVariable >= 2 ? aCoverVariable == 2
            : !(aTileEntity instanceof IMachineProgress)
                || ((IMachineProgress) aTileEntity).isAllowedToWork() == (aCoverVariable % 2 == 0);
    }

    @Override
    public boolean letsItemsIn(ForgeDirection side, int aCoverID, int aCoverVariable, int aSlot,
        ICoverable aTileEntity) {
        return aCoverVariable >= 2 ? aCoverVariable == 3
            : !(aTileEntity instanceof IMachineProgress)
                || ((IMachineProgress) aTileEntity).isAllowedToWork() == (aCoverVariable % 2 == 0);
    }

    @Override
    public boolean letsItemsOut(ForgeDirection side, int aCoverID, int aCoverVariable, int aSlot,
        ICoverable aTileEntity) {
        return aCoverVariable >= 2 ? aCoverVariable == 2
            : !(aTileEntity instanceof IMachineProgress)
                || ((IMachineProgress) aTileEntity).isAllowedToWork() == (aCoverVariable % 2 == 0);
    }

    @Override
    public boolean alwaysLookConnected(ForgeDirection side, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return true;
    }

    @Override
    public int getTickRate(ForgeDirection side, int aCoverID, int aCoverVariable, ICoverable aTileEntity) {
        return 0;
    }

    // GUI stuff

    @Override
    public boolean hasCoverGUI() {
        return true;
    }

    @Override
    public ModularWindow createWindow(CoverUIBuildContext buildContext) {
        return new ShutterUIFactory(buildContext).createWindow();
    }

    private class ShutterUIFactory extends UIFactory {

        private static final int startX = 10;
        private static final int startY = 25;
        private static final int spaceX = 18;
        private static final int spaceY = 18;

        public ShutterUIFactory(CoverUIBuildContext buildContext) {
            super(buildContext);
        }

        @SuppressWarnings("PointlessArithmeticExpression")
        @Override
        protected void addUIWidgets(ModularWindow.Builder builder) {
            builder
                .widget(
                    new CoverDataControllerWidget.CoverDataIndexedControllerWidget_ToggleButtons<>(
                        this::getCoverData,
                        this::setCoverData,
                        CoverShutter.this,
                        (index, coverData) -> index == convert(coverData),
                        (index, coverData) -> new ISerializableObject.LegacyCoverData(index))
                            .addToggleButton(
                                0,
                                CoverDataFollowerToggleButtonWidget.ofCheck(),
                                widget -> widget.setPos(spaceX * 0, spaceY * 0))
                            .addToggleButton(
                                1,
                                CoverDataFollowerToggleButtonWidget.ofCheck(),
                                widget -> widget.setPos(spaceX * 0, spaceY * 1))
                            .addToggleButton(
                                2,
                                CoverDataFollowerToggleButtonWidget.ofCheck(),
                                widget -> widget.setPos(spaceX * 0, spaceY * 2))
                            .addToggleButton(
                                3,
                                CoverDataFollowerToggleButtonWidget.ofCheck(),
                                widget -> widget.setPos(spaceX * 0, spaceY * 3))
                            .setPos(startX, startY))
                .widget(
                    new TextWidget(GTUtility.trans("082", "Open if work enabled"))
                        .setDefaultColor(COLOR_TEXT_GRAY.get())
                        .setPos(3 + startX + spaceX * 1, 4 + startY + spaceY * 0))
                .widget(
                    new TextWidget(GTUtility.trans("083", "Open if work disabled"))
                        .setDefaultColor(COLOR_TEXT_GRAY.get())
                        .setPos(3 + startX + spaceX * 1, 4 + startY + spaceY * 1))
                .widget(
                    new TextWidget(GTUtility.trans("084", "Only Output allowed")).setDefaultColor(COLOR_TEXT_GRAY.get())
                        .setPos(3 + startX + spaceX * 1, 4 + startY + spaceY * 2))
                .widget(
                    new TextWidget(GTUtility.trans("085", "Only Input allowed")).setDefaultColor(COLOR_TEXT_GRAY.get())
                        .setPos(3 + startX + spaceX * 1, 4 + startY + spaceY * 3));
        }
    }
}