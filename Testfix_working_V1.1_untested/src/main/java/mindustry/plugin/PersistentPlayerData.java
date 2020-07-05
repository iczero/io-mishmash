package mindustry.plugin;
import arc.struct.Array;
import mindustry.entities.bullet.BulletType;
import mindustry.entities.type.BaseUnit;
import mindustry.entities.type.Player;

import java.io.Serializable;
import java.lang.ref.WeakReference;

public class PersistentPlayerData implements Serializable {
    public String origName;
    public Array<BaseUnit> draugPets = new Array<>();
    public int bbIncrementor = 0;
    public boolean spawnedLichPet;
    public boolean spawnedPowerGen;

    public PersistentPlayerData() {}

}
