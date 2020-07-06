package mindustry.plugin;

import arc.math.Mathf;
import arc.util.Log;
import arc.util.Time;
import arc.util.Timer;
import mindustry.Vars;
import mindustry.content.Blocks;
import mindustry.entities.type.Player;
import mindustry.entities.type.TileEntity;
import mindustry.gen.Call;
import mindustry.maps.Map;
import mindustry.net.Administration;
import mindustry.world.Tile;
import mindustry.world.blocks.storage.CoreBlock;

import static mindustry.Vars.state;
import static mindustry.Vars.world;
import static mindustry.plugin.Utils.*;

// TODO: this isn't used?
public class MapRules {

    public static void onMapLoad(){

        // action filter
        Vars.netServer.admins.addActionFilter(action -> {
            Player player = action.player;
            if (player == null) return true;

            // disable checks for admins
            if (player.isAdmin) return true;

            return action.type != Administration.ActionType.rotate;
        });

        Map map = world.getMap();

        // spawn all players quick for the first time
        float orig = state.rules.respawnTime;
        state.rules.respawnTime = 0.25f;
        Call.onSetRules(state.rules);
        Timer.schedule(() -> {
            state.rules.respawnTime = orig;
            Call.onSetRules(state.rules);
        }, 5f);


        // display map description on core tiles for the first minute
        Call.onInfoToast("Playing [accent]" + escapeColorCodes(map.name()) + "[] by[accent] " + map.author(), 20f); // credit map makers

        if(map.description().equals("???unknown???")) return;
        Tile[][] tiles = world.getTiles();
        for (int x = 0; x < tiles.length; ++x) {
            for(int y = 0; y < tiles[0].length; ++y) {
                if (tiles[x][y] != null) {
                    TileEntity ent = tiles[x][y].ent();
                    if (ent instanceof CoreBlock.CoreEntity) {
                        Call.onLabel(map.description(), 20f, ent.x, ent.y);
                    }
                }
            }
        }
    }

    public static void run(){
        onMapLoad();

        for (java.util.Map.Entry<String, PersistentPlayerData> entry : ioMain.playerDataGroup.entrySet()) {
            PersistentPlayerData tdata = entry.getValue();
            if(tdata != null) {
                tdata.spawnedPowerGen = false;
                tdata.spawnedLichPet = false;
                tdata.draugPets.clear();
            }
        }


        Map map = world.getMap();
        switch(map.name()){
            case "Minefield":
                Log.info("[MapRules]: Minefield action trigerred.");
                Call.sendMessage("[accent]Preparing minefield map, please wait.");

                for(int x = 0; x < world.width(); x++){
                    for(int y = 0; y < world.height(); y++){
                        Tile tile = world.tile(x, y);
                        if(tile.block() == Blocks.shockMine){
                            Time.run(Mathf.random(60f * 60f * 10), () -> { // clear out all mines after 10 minutes.
                                if(tile.block() == Blocks.shockMine && map.name().equals("Minefield")){ // check if mine still exists & map is still minefield lol
                                    tile.entity.kill();
                                }
                            });
                        }
                    }
                }
                Call.sendMessage("[accent]Map script loaded. Good luck!");
                break;
            case "test": // feel free to add your own scripts for your custom maps!
                Log.info("Test map loaded.");
                break;

        }
    }
}
