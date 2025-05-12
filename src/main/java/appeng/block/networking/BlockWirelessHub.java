package appeng.block.networking;

import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;

import appeng.client.texture.WirelessTextures;
import appeng.tile.networking.TileWirelessHub;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BlockWirelessHub extends BlockWirelessConnector {

    public BlockWirelessHub() {
        super();
        this.setTileEntity(TileWirelessHub.class);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int direction, int metadata) {
        return switch (metadata) {
            case 1 -> WirelessTextures.WirelessHubWhite.getOnIcon();
            case 2 -> WirelessTextures.WirelessHubOrange.getOnIcon();
            case 3 -> WirelessTextures.WirelessHubMagenta.getOnIcon();
            case 4 -> WirelessTextures.WirelessHubLightBlue.getOnIcon();
            case 5 -> WirelessTextures.WirelessHubYellow.getOnIcon();
            case 6 -> WirelessTextures.WirelessHubLime.getOnIcon();
            case 7 -> WirelessTextures.WirelessHubPink.getOnIcon();
            case 8 -> WirelessTextures.WirelessHubGrey.getOnIcon();
            case 9 -> WirelessTextures.WirelessHubLightGrey.getOnIcon();
            case 10 -> WirelessTextures.WirelessHubCyan.getOnIcon();
            case 11 -> WirelessTextures.WirelessHubPurple.getOnIcon();
            case 12 -> WirelessTextures.WirelessHubBlue.getOnIcon();
            case 13 -> WirelessTextures.WirelessHubBrown.getOnIcon();
            case 14 -> WirelessTextures.WirelessHubGreen.getOnIcon();
            case 15 -> WirelessTextures.WirelessHubRed.getOnIcon();
            case 16 -> WirelessTextures.WirelessHubBlack.getOnIcon();
            default -> WirelessTextures.WirelessHubTransparent.getOnIcon();
        };
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(IBlockAccess w, int x, int y, int z, int s) {
        if (w.getBlockMetadata(x, y, z) == 0) {
            return switch (((TileWirelessHub) w.getTileEntity(x, y, z)).getColor()) {
                case White -> WirelessTextures.WirelessHubWhite.getOffIcon();
                case Orange -> WirelessTextures.WirelessHubOrange.getOffIcon();
                case Magenta -> WirelessTextures.WirelessHubMagenta.getOffIcon();
                case LightBlue -> WirelessTextures.WirelessHubLightBlue.getOffIcon();
                case Yellow -> WirelessTextures.WirelessHubYellow.getOffIcon();
                case Lime -> WirelessTextures.WirelessHubLime.getOffIcon();
                case Pink -> WirelessTextures.WirelessHubPink.getOffIcon();
                case Gray -> WirelessTextures.WirelessHubGrey.getOffIcon();
                case LightGray -> WirelessTextures.WirelessHubLightGrey.getOffIcon();
                case Cyan -> WirelessTextures.WirelessHubCyan.getOffIcon();
                case Purple -> WirelessTextures.WirelessHubPurple.getOffIcon();
                case Blue -> WirelessTextures.WirelessHubBlue.getOffIcon();
                case Brown -> WirelessTextures.WirelessHubBrown.getOffIcon();
                case Green -> WirelessTextures.WirelessHubGreen.getOffIcon();
                case Red -> WirelessTextures.WirelessHubRed.getOffIcon();
                case Black -> WirelessTextures.WirelessHubBlack.getOffIcon();
                default -> WirelessTextures.WirelessHubTransparent.getOffIcon();
            };
        } else {
            return switch (((TileWirelessHub) w.getTileEntity(x, y, z)).getColor()) {
                case White -> WirelessTextures.WirelessHubWhite.getOnIcon();
                case Orange -> WirelessTextures.WirelessHubOrange.getOnIcon();
                case Magenta -> WirelessTextures.WirelessHubMagenta.getOnIcon();
                case LightBlue -> WirelessTextures.WirelessHubLightBlue.getOnIcon();
                case Yellow -> WirelessTextures.WirelessHubYellow.getOnIcon();
                case Lime -> WirelessTextures.WirelessHubLime.getOnIcon();
                case Pink -> WirelessTextures.WirelessHubPink.getOnIcon();
                case Gray -> WirelessTextures.WirelessHubGrey.getOnIcon();
                case LightGray -> WirelessTextures.WirelessHubLightGrey.getOnIcon();
                case Cyan -> WirelessTextures.WirelessHubCyan.getOnIcon();
                case Purple -> WirelessTextures.WirelessHubPurple.getOnIcon();
                case Blue -> WirelessTextures.WirelessHubBlue.getOnIcon();
                case Brown -> WirelessTextures.WirelessHubBrown.getOnIcon();
                case Green -> WirelessTextures.WirelessHubGreen.getOnIcon();
                case Red -> WirelessTextures.WirelessHubRed.getOnIcon();
                case Black -> WirelessTextures.WirelessHubBlack.getOnIcon();
                default -> WirelessTextures.WirelessHubTransparent.getOnIcon();
            };
        }
    }
}
