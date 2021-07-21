using UnityEngine;
using System.Collections;

namespace packt.PhotoEra.Database
{
    public class Photo
    {
        public Mapping.MapLocation location;
        public Vector3 position;
        public double spawnTimestamp;
        public double lastSeenTimestamp;
        public GameObject gameObject;
        public string pid;

    }
}
