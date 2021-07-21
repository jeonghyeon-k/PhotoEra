using System.Collections;
using System.Collections.Generic;
using System;
using UnityEngine;
using UnityEngine.UI;
using packt.PhotoEra.Mapping;
using packt.PhotoEra.Database;
using packt.PhotoEra.Utils;


namespace packt.PhotoEra.Services {

public class PhotoService : MonoBehaviour
{
    private AndroidJavaObject currentActivity;
    public AndroidJavaClass ajc;
    public Texture photoTexture;
    public GameObject cubePrefab;

    public GPSLocationService gpsLocationService;
    public List<Photo> photos;
    public List<Pdata> pdatas;
    public int size;
    public string phid;
    public float plat;
    public float plon;
    private double lastTimestamp;

    [Header("Phone Visibility")]
    public float photoSeeDistance = 150f;
    
    // Start is called before the first frame update
    void Start()
    {
        pdatas = new List<Pdata>();
        ajc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        currentActivity = ajc.GetStatic<AndroidJavaObject>("currentActivity");
        

        if(currentActivity != null)
         {
             size = currentActivity.Call<int>("getSize");

             for (int i =0; i<size ; i++){

             phid = currentActivity.Call<string>("getPID",i);
             plat = currentActivity.Call<float>("getLat",i);
             plon = currentActivity.Call<float>("getLon",i);

             var pdata = new Pdata {
                 pid = phid,
                 lat = plat,
                 lon = plon
             };

             pdatas.Add(pdata);
             }
         }
        photos = new List<Photo>();
        float latDo;
        float lonDo;
        for (int i = 0 ; i< pdatas.Count ; i++){
            latDo = Convert.ToSingle(pdatas[i].lat);
            lonDo = Convert.ToSingle(pdatas[i].lon);

            var photo = new Photo
            {
                location = new MapLocation(lonDo, latDo),
                spawnTimestamp = gpsLocationService.PlayerTimestamp,
                pid = pdatas[i].pid
            };
            photos.Add(photo);
        }
        gpsLocationService.OnMapRedraw += GpsLocationService_OnMapRedraw;
        // for(int i = 0; i<5; i++){
        // GameObject cube = GameObject.CreatePrimitive(PrimitiveType.Cube);
        // cube.transform.position = new Vector3(1F,1F,1F+i);
        // cube.transform.localScale = new Vector3(0.2F,0.2F,0.2F);
        // photo = Resources.Load("Texture/gyu") as Texture;

        // renderer = cube.GetComponent<MeshRenderer>();
        // renderer.material.mainTexture = photo;
        // }
        
        
    }
    
    private void GpsLocationService_OnMapRedraw(GameObject g)
        {
            //map is recentered, recenter all photos
            foreach(Photo p in photos)
            {
                if(p.gameObject != null)
                {
                    var newPosition = ConvertToWorldSpace(p.location.Longitude, p.location.Latitude);
                    p.gameObject.transform.position = newPosition;
                }
            }
        }
        

    // Update is called once per frame
    void Update()
    {
        if (gpsLocationService != null &&
                gpsLocationService.IsServiceStarted &&
                gpsLocationService.PlayerTimestamp > lastTimestamp)
            {
                lastTimestamp = gpsLocationService.PlayerTimestamp;
                //test code
                var s = MathG.Distance(gpsLocationService.Longitude, gpsLocationService.Latitude, gpsLocationService.mapCenter.Longitude, gpsLocationService.mapCenter.Latitude);
                print("Player Distance from map tile center = " + s);

                //update the photos around the player
                CheckPhotos();
            }
            
            
    }

     private void CheckPhotos()
        {       
            //store players location for easy access in distance calculations
            var playerLocation = new MapLocation(gpsLocationService.Longitude, gpsLocationService.Latitude);
           
            //get the current Epoch time in seconds
            var now = Epoch.Now;

            foreach (Photo p in photos)
            {
                var d = MathG.Distance(p.location, playerLocation);
                if (MathG.Distance(p.location, playerLocation) < photoSeeDistance)
                {
                    p.lastSeenTimestamp = now;
                    if (p.gameObject == null)
                    {
                        print("Monster seen, distance " + d + " started at " + p.spawnTimestamp);
                        SpawnPhoto(p);
                    }
                    else
                    {
                        p.gameObject.SetActive(true);  //make sure the monster is visible
                    }                        
                    continue;
                }

                //hide monsters that can't be seen 
                if(p.gameObject != null)
                {
                    p.gameObject.SetActive(false);
                }

        
            }       
            
        }
        
        private Vector3 ConvertToWorldSpace(float longitude, float latitude)
        {
            //convert GPS lat/long to world x/y 
            var x = ((GoogleMapUtils.LonToX(longitude)
                - gpsLocationService.mapWorldCenter.x) * gpsLocationService.mapScale.x);
            var y = (GoogleMapUtils.LatToY(latitude)
                - gpsLocationService.mapWorldCenter.y) * gpsLocationService.mapScale.y;
            return new Vector3(-x, 0.5f, y);
        }

        private void SpawnPhoto(Photo photo)
        {
            var lon = photo.location.Longitude;
            var lat = photo.location.Latitude;
            var position = ConvertToWorldSpace(lon, lat);
            // photo.gameObject = GameObject.CreatePrimitive(PrimitiveType.Cube);
            // photo.gameObject.transform.position = position;
            // photo.gameObject.transform.localScale = new Vector3(0.3F,0.3F,0.3F);
        
            photo.gameObject = (GameObject)Instantiate(cubePrefab,position,cubePrefab.transform.rotation);
            SeePhoto cs = photo.gameObject.GetComponent<SeePhoto>();
            cs.pid = photo.pid;


            // photo.gameobject = (GameObject)Instantiate(cubePrefab,new Vector3(0, 1, 0), Quaternion.identity);
            // photo.gameObject.transform.position = position;
        
            // photoTexture = Resources.Load("Texture/question-mark") as Texture;

			//when the image returns set it as the tile texture
			// Renderer renderer = photo.gameObject.GetComponent<MeshRenderer>();
            // renderer.material.mainTexture = photoTexture;
        }

}
}
