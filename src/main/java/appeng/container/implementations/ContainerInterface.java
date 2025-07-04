/*
 * This file is part of Applied Energistics 2. Copyright (c) 2013 - 2014, AlgorithmX2, All rights reserved. Applied
 * Energistics 2 is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any
 * later version. Applied Energistics 2 is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General
 * Public License for more details. You should have received a copy of the GNU Lesser General Public License along with
 * Applied Energistics 2. If not, see <http://www.gnu.org/licenses/lgpl>.
 */

package appeng.container.implementations;

import java.util.ArrayList;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import appeng.api.config.AdvancedBlockingMode;
import appeng.api.config.FuzzyMode;
import appeng.api.config.InsertionMode;
import appeng.api.config.LockCraftingMode;
import appeng.api.config.SecurityPermissions;
import appeng.api.config.Settings;
import appeng.api.config.Upgrades;
import appeng.api.config.YesNo;
import appeng.api.implementations.ICraftingPatternItem;
import appeng.api.networking.crafting.ICraftingPatternDetails;
import appeng.api.util.IConfigManager;
import appeng.container.guisync.GuiSync;
import appeng.container.slot.IOptionalSlotHost;
import appeng.container.slot.OptionalSlotFake;
import appeng.container.slot.OptionalSlotRestrictedInput;
import appeng.container.slot.SlotNormal;
import appeng.container.slot.SlotRestrictedInput;
import appeng.helpers.DualityInterface;
import appeng.helpers.IInterfaceHost;
import appeng.me.cache.CraftingGridCache;
import appeng.util.PatternMultiplierHelper;
import appeng.util.Platform;

public class ContainerInterface extends ContainerUpgradeable implements IOptionalSlotHost {

    private final DualityInterface myDuality;

    @GuiSync(3)
    public YesNo bMode = YesNo.NO;

    @GuiSync(15)
    public YesNo sbMode = YesNo.NO;

    @GuiSync(4)
    public YesNo iTermMode = YesNo.YES;

    @GuiSync(14)
    public boolean isAllowedToMultiplyPatterns = false;

    @GuiSync(13)
    public YesNo patternOptimization = YesNo.YES;

    @GuiSync(10)
    public AdvancedBlockingMode advancedBlockingMode = AdvancedBlockingMode.DEFAULT;

    @GuiSync(12)
    public LockCraftingMode lockCraftingMode = LockCraftingMode.NONE;

    @GuiSync(16)
    public FuzzyMode fuzzyMode = FuzzyMode.IGNORE_ALL;

    @GuiSync(8)
    public InsertionMode insertionMode = InsertionMode.DEFAULT;

    @GuiSync(7)
    public int patternRows;

    @GuiSync(17)
    public int configSlots;

    @GuiSync(9)
    public boolean isEmpty;

    @GuiSync(18)
    public boolean isConfigEmpty;

    public ContainerInterface(final InventoryPlayer ip, final IInterfaceHost te) {
        super(ip, te.getInterfaceDuality().getHost());

        this.myDuality = te.getInterfaceDuality();
        patternRows = getPatternCapacityCardsInstalled();
        configSlots = getConfigSlotsEnabled();

        for (int row = 0; row < 4; ++row) {
            for (int x = 0; x < DualityInterface.NUMBER_OF_PATTERN_SLOTS; x++) {
                this.addSlotToContainer(
                        new OptionalSlotRestrictedInput(
                                SlotRestrictedInput.PlacableItemType.ENCODED_PATTERN,
                                this.myDuality.getPatterns(),
                                this,
                                x + row * DualityInterface.NUMBER_OF_PATTERN_SLOTS,
                                8 + 18 * x,
                                108 - row * 18,
                                row,
                                this.getInventoryPlayer()).setStackLimit(1));
            }
        }

        for (int x = 0; x < DualityInterface.NUMBER_OF_CONFIG_SLOTS; x++) {
            this.addSlotToContainer(new OptionalSlotFake(this.myDuality.getConfig(), this, x, 8 + 18 * x, 15, 0));
        }

        for (int x = 0; x < DualityInterface.NUMBER_OF_STORAGE_SLOTS; x++) {
            this.addSlotToContainer(new SlotNormal(this.myDuality.getStorage(), x, 8 + 18 * x, 15 + 18));
        }
    }

    @Override
    protected int getHeight() {
        return 211;
    }

    @Override
    protected void setupConfig() {
        this.setupUpgrades();
    }

    @Override
    public int availableUpgrades() {
        return 4;
    }

    @Override
    public void onUpdate(final String field, final Object oldValue, final Object newValue) {
        super.onUpdate(field, oldValue, newValue);
        if (Platform.isClient() && field.equals("patternRows")) getRemovedPatterns();
        if (Platform.isClient() && field.equals("configSlots")) getRemovedConfig();
    }

    @Override
    public void detectAndSendChanges() {
        this.verifyPermissions(SecurityPermissions.BUILD, false);

        this.isAllowedToMultiplyPatterns = true;

        if (patternRows != getPatternCapacityCardsInstalled()) patternRows = getPatternCapacityCardsInstalled();
        isEmpty = patternRows == -1;

        if (configSlots != getConfigSlotsEnabled()) configSlots = getConfigSlotsEnabled();
        isConfigEmpty = configSlots == -1;

        final ArrayList<ItemStack> drops = getRemovedPatterns();
        if (!drops.isEmpty()) {
            TileEntity te = myDuality.getHost().getTile();
            if (te != null) Platform.spawnDrops(te.getWorldObj(), te.xCoord, te.yCoord, te.zCoord, drops);
        }
        super.detectAndSendChanges();
    }

    private ArrayList<ItemStack> getRemovedPatterns() {
        final ArrayList<ItemStack> drops = new ArrayList<>();
        for (final Object o : this.inventorySlots) {
            if (o instanceof OptionalSlotRestrictedInput fs) {
                if (!fs.isEnabled()) {
                    ItemStack s = fs.inventory.getStackInSlot(fs.getSlotIndex());
                    if (s != null) {
                        drops.add(s);
                        fs.inventory.setInventorySlotContents(fs.getSlotIndex(), null);
                        fs.clearStack();
                    }
                }
            }
        }
        return drops;
    }

    private void getRemovedConfig() {
        for (final Object o : this.inventorySlots) {
            if (o instanceof OptionalSlotFake fs) {
                if (!fs.isEnabled()) {
                    fs.inventory.setInventorySlotContents(fs.getSlotIndex(), null);
                    fs.clearStack();
                }
            }
        }
    }

    @Override
    protected void loadSettingsFromHost(final IConfigManager cm) {
        this.setBlockingMode((YesNo) cm.getSetting(Settings.BLOCK));
        this.setSmartBlockingMode((YesNo) cm.getSetting(Settings.SMART_BLOCK));
        this.setInterfaceTerminalMode((YesNo) cm.getSetting(Settings.INTERFACE_TERMINAL));
        this.setInsertionMode((InsertionMode) cm.getSetting(Settings.INSERTION_MODE));
        this.setPatternOptimization((YesNo) cm.getSetting(Settings.PATTERN_OPTIMIZATION));
        this.setAdvancedBlockingMode((AdvancedBlockingMode) cm.getSetting(Settings.ADVANCED_BLOCKING_MODE));
        this.setLockCraftingMode((LockCraftingMode) cm.getSetting(Settings.LOCK_CRAFTING_MODE));
        this.setFuzzyMode((FuzzyMode) cm.getSetting(Settings.FUZZY_MODE));
    }

    public void doublePatterns(int val) {
        if (!this.isAllowedToMultiplyPatterns) return;
        boolean fast = (val & 1) != 0;
        boolean backwards = (val & 2) != 0;
        CraftingGridCache.pauseRebuilds();
        try {
            IInventory patterns = this.myDuality.getPatterns();
            TileEntity te = this.myDuality.getHost().getTile();
            for (int i = 0; i < patterns.getSizeInventory(); i++) {
                ItemStack stack = patterns.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ICraftingPatternItem cpi) {
                    ICraftingPatternDetails details = cpi.getPatternForItem(stack, te.getWorldObj());
                    if (details != null && !details.isCraftable()) {
                        int max = backwards ? PatternMultiplierHelper.getMaxBitDivider(details)
                                : PatternMultiplierHelper.getMaxBitMultiplier(details);
                        if (max > 0) {
                            ItemStack copy = stack.copy();
                            PatternMultiplierHelper
                                    .applyModification(copy, (fast ? Math.min(3, max) : 1) * (backwards ? -1 : 1));
                            patterns.setInventorySlotContents(i, copy);
                        }
                    }
                }
            }
        } catch (Throwable ignored) {}
        CraftingGridCache.unpauseRebuilds();
        this.standardDetectAndSendChanges();
    }

    public YesNo getBlockingMode() {
        return this.bMode;
    }

    private void setBlockingMode(final YesNo bMode) {
        this.bMode = bMode;
    }

    public YesNo getSmartBlockingMode() {
        return this.sbMode;
    }

    private void setSmartBlockingMode(final YesNo sbMode) {
        this.sbMode = sbMode;
    }

    public YesNo getInterfaceTerminalMode() {
        return this.iTermMode;
    }

    private void setInterfaceTerminalMode(final YesNo iTermMode) {
        this.iTermMode = iTermMode;
    }

    public YesNo getPatternOptimization() {
        return patternOptimization;
    }

    public void setPatternOptimization(final YesNo patternOptimization) {
        this.patternOptimization = patternOptimization;
    }

    public InsertionMode getInsertionMode() {
        return this.insertionMode;
    }

    private void setInsertionMode(final InsertionMode insertionMode) {
        this.insertionMode = insertionMode;
    }

    public AdvancedBlockingMode getAdvancedBlockingMode() {
        return this.advancedBlockingMode;
    }

    private void setAdvancedBlockingMode(final AdvancedBlockingMode mode) {
        this.advancedBlockingMode = mode;
    }

    public LockCraftingMode getLockCraftingMode() {
        return this.lockCraftingMode;
    }

    private void setLockCraftingMode(LockCraftingMode mode) {
        this.lockCraftingMode = mode;
    }

    public FuzzyMode getFuzzyMode() {
        return this.fuzzyMode;
    }

    public void setFuzzyMode(FuzzyMode mode) {
        this.fuzzyMode = mode;
    }

    public int getPatternCapacityCardsInstalled() {
        if (Platform.isClient() && isEmpty) return -1;
        if (myDuality == null) return 0;
        return myDuality.getInstalledUpgrades(Upgrades.PATTERN_CAPACITY);
    }

    private int getConfigSlotsEnabled() {
        if (Platform.isClient() && isConfigEmpty) return -1;
        return myDuality.getConfigSize();
    }

    @Override
    public boolean isSlotEnabled(final int idx) {
        if (Platform.isClient() && (isEmpty || isConfigEmpty)) return false;
        return myDuality.getInstalledUpgrades(Upgrades.PATTERN_CAPACITY) >= idx;
    }
}
