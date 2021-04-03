package xyz.spudpvp.ccevent.command;

import xyz.jakemt04.gapi.GCommand;

import java.util.Collections;

public class EventCommand extends GCommand {
    public EventCommand() {
        super(
                "event",
                "Event Management Commands",
                "spudpvp.event",
                Collections.emptyList(),
                true
        );
        registerSubCommand(new AddMemberCommand());
        registerSubCommand(new CreateTeamCommand());
        registerSubCommand(new RemoveMemberCommand());
        registerSubCommand(new ModifyPointsCommand());
    }


}
