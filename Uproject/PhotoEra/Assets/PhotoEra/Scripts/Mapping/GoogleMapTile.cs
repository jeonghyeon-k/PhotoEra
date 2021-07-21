using UnityEngine;
using System.Collections;
using packt.PhotoEra.Services;

namespace packt.PhotoEra.Mapping
{
    [AddComponentMenu("Mapping/GoogleMapTile")]
    public class GoogleMapTile : MonoBehaviour
	{
		public enum MapType
		{
			RoadMap,
			Satellite,
			Terrain,
			Hybrid
		}

		//Google Maps API Staticmap URL
		// private const string GOOGLE_MAPS_URL = "https://maps.googleapis.com/maps/api/staticmap";
		private const string GOOGLE_MAPS_URL = "https://api.mapbox.com/styles/v1/ssu912/ck1g1cik90y5d1cqtjia5c0mn/static" ;
		[Header("Map Settings")]
		[Range(1,20)]
		[Tooltip("Zoom Level, 1=global - 20=house")]
		public int zoomLevel = 1;
		[Tooltip("Type of map, Road, Satellite, Terrain or Hybrid")]
		public MapType mapType = MapType.RoadMap;
		[Range(64,1024)]
		[Tooltip("Size in pixels of the map image")]
		public int size = 640;
		[Tooltip("Double the pixel resolution of the image returned")]
		public bool doubleResolution = true;
		[Tooltip("Defines the origin of the map")]
		public MapLocation worldCenterLocation;

		[Header("Tile Settings")]
		[Tooltip("Sets the tiles position in tile units")]        
        public Vector2 TileOffset;
		[Tooltip("Calculated tile center")]
		public MapLocation tileCenterLocation;
		[Tooltip("Calculated tile corners")]
        public Vector2 TopLeftCorner;
        public Vector2 BottomRightCorner;

		[Header("GPS Settings")]
		[Tooltip("GPS service used to locate world center")]
		public GPSLocationService gpsLocationService;
        private double lastGPSUpdate;

		// Use this for initialization
		void Start ()
		{
			RefreshMapTile ();
		}
	
		// Update is called once per frame
		void Update ()
		{
			//check if a new location has been acquired
            if (gpsLocationService != null &&
                gpsLocationService.IsServiceStarted && 
                lastGPSUpdate < gpsLocationService.Timestamp)
            {
                lastGPSUpdate = gpsLocationService.Timestamp;
                worldCenterLocation.Latitude = gpsLocationService.Latitude;
                worldCenterLocation.Longitude = gpsLocationService.Longitude;
                print("GoogleMapTile refreshing map texture");
                RefreshMapTile();
            }
		}

		public void RefreshMapTile() {
			
			StartCoroutine(_RefreshMapTile());
		}

		IEnumerator _RefreshMapTile ()
		{            
			//find the center lat/long of the tile
			tileCenterLocation.Latitude = GoogleMapUtils.adjustLatByPixels(worldCenterLocation.Latitude, (int)(size * 1 * TileOffset.y), zoomLevel);
			tileCenterLocation.Longitude = GoogleMapUtils.adjustLonByPixels(worldCenterLocation.Longitude, (int)(size * 1 * TileOffset.x), zoomLevel);

			var queryString = "";

			queryString += WWW.UnEscapeURL (string.Format ("{1},{0}", tileCenterLocation.Latitude, tileCenterLocation.Longitude));
			queryString += "," +zoomLevel.ToString();
			queryString += ",0,0";
			queryString += "/" + WWW.UnEscapeURL (string.Format ("{0}x{0}", size));
			queryString += "@2x";

			//build the query string parameters for the map tile request
			// queryString += "center=" + WWW.UnEscapeURL (string.Format ("{0},{1}", tileCenterLocation.Latitude, tileCenterLocation.Longitude));
			// queryString += "&zoom=" + zoomLevel.ToString ();
			// queryString += "&size=" + WWW.UnEscapeURL (string.Format ("{0}x{0}", size));
			// queryString += "&scale=" + (doubleResolution ? "2" : "1");
			// queryString += "&maptype=" + mapType.ToString ().ToLower ();
			// queryString += "&format=" + "png";
            // queryString += "&key=AIzaSyDr-_cZq95gfFj8R-pOZmtggScB03iInR0";

            // // adding the map styles
            // queryString += "&style=element:geometry|invert_lightness:true|weight:3.1|hue:0x00ffd5";
            // queryString += "&style=element:labels|visibility:off";

            queryString += "?access_token=pk.eyJ1Ijoic3N1OTEyIiwiYSI6ImNrMWVxMmoxejA0bXEzbm5ydXUyODZuOHQifQ.2MG2G9EkZUOUNA3yyjvYEw";

            //check if script is on a mobile device and using a location service 
//             var usingSensor = false;
// #if MOBILE_INPUT
//             usingSensor = Input.location.isEnabledByUser 
// 							&& Input.location.status == LocationServiceStatus.Running 
// 							&& gpsLocationService !=null;
// #endif
// 			queryString += "&sensor=" + (usingSensor ? "true" : "false");

			//set map bounds rect
			TopLeftCorner.x = GoogleMapUtils.adjustLonByPixels(tileCenterLocation.Longitude, -size, zoomLevel);
			TopLeftCorner.y = GoogleMapUtils.adjustLatByPixels(tileCenterLocation.Latitude, size, zoomLevel);

			BottomRightCorner.x = GoogleMapUtils.adjustLonByPixels(tileCenterLocation.Longitude, size, zoomLevel);
			BottomRightCorner.y = GoogleMapUtils.adjustLatByPixels(tileCenterLocation.Latitude, -size, zoomLevel);

            print(string.Format("Tile {0}x{1} requested with {2}", TileOffset.x, TileOffset.y, queryString));

			//finally, we request the image
			// var req = new WWW(GOOGLE_MAPS_URL + "?" + queryString);
			var req = new WWW(GOOGLE_MAPS_URL + "/" + queryString);
			// Debug.Log(GOOGLE_MAPS_URL + "/" + queryString);

			//yield until the service responds
			yield return req;
			//first destroy the old texture first
			Destroy(GetComponent<Renderer>().material.mainTexture);
			//when the image returns set it as the tile texture
			GetComponent<Renderer>().material.mainTexture = req.texture;
            print(string.Format("Tile {0}x{1} textured", TileOffset.x, TileOffset.y));
            if(TileOffset.x == 0 && TileOffset.y == 0)
            {
                gpsLocationService.MapRedrawn();
            }
        }
	}
}
