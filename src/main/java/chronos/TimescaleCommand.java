package chronos;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class TimescaleCommand extends CommandBase {

    @Override
    public String getCommandName() {
        return "timescale";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/timescale set <scale>";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText(String.format("Time scale is %s", WorldHandler.getScale(sender.getEntityWorld()))));
        } else if (args.length == 2 && args[0].equals("set")) {
            double scale = -1;
            try {
                scale = Double.parseDouble(args[1]);
            } catch (NumberFormatException ex) {
                scale = -1;
            }
            if (scale >= 0) {
                WorldHandler.setScale(sender.getEntityWorld(), scale);
                sender.addChatMessage(new ChatComponentText(String.format("Time scale set to %s", WorldHandler.getScale(sender.getEntityWorld()))));
            } else {
                sender.addChatMessage(new ChatComponentText("Invalid time scale"));
            }
        } else {
            sender.addChatMessage(new ChatComponentText(getCommandUsage(sender)));
        }
    }
}
