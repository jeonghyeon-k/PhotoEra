using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using packt.PhotoEra.Database;

public class SeePhoto : MonoBehaviour
{
    private AndroidJavaObject currentActivity;
    private GameObject target;
    // private int sysNumber = 2;
    public string pid;

    public void Start() {
        AndroidJavaClass ajc = new AndroidJavaClass("com.unity3d.player.UnityPlayer");
        currentActivity = ajc.GetStatic<AndroidJavaObject>("currentActivity");
    }

    public void Update() {
        if(Input.GetMouseButton(0)) {
            target = GetClickedObject() ;
            if(target.Equals(gameObject))  //선택된게 나라면
            {
                  if(currentActivity != null)
                  {
                    currentActivity.Call("SeePhoto",pid);
                    Debug.Log("mouse Click = " + pid);
                    }
                    Application.Quit();
            }        
       
        }  
}


private GameObject GetClickedObject() 
{
        RaycastHit hit;

        GameObject target = null; 
        Ray ray = Camera.main.ScreenPointToRay(Input.mousePosition); //마우스 포인트 근처 좌표를 만든다. 

        if( true == (Physics.Raycast(ray.origin, ray.direction * 10, out hit)))   //마우스 근처에 오브젝트가 있는지 확인
        {
            //있으면 오브젝트를 저장한다.
            target = hit.collider.gameObject; 
        } 

        return target; 
}
}
