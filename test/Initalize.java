package events;

import com.fasterxml.jackson.databind.JsonNode;

import akka.actor.ActorRef;
import commands.BasicCommands;
import commands.TrackingTell;
import demo.CommandDemo;
import structures.GameState;

public class Initalize implements EventProcessor {

    @Override
    public void processEvent(ActorRef out, GameState gameState, JsonNode message) {

        // Capture demo-created tiles/units into gameState
        BasicCommands.altTell = new TrackingTell(out, gameState);

        CommandDemo.executeDemo(out);

        // Stop intercepting after demo setup
        BasicCommands.altTell = null;

        gameState.gameInitalised = true;
    }
}