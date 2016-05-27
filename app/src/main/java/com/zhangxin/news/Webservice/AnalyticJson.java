package com.zhangxin.news.Webservice;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2015/11/13.
 */
public class AnalyticJson {

    //解析多个Json的数据，顺序显示
    public static void parseJsonMultiImg_Order(String strResult, List list, List adList, String JsonArray, boolean isFirst) {
        try {
            JSONObject jsonObj = new JSONObject(strResult);
            JSONArray jsonArray = jsonObj.getJSONArray(JsonArray);
            //解析的Json串顺序排列
            for (int i = 0; i < jsonArray.length(); i++) {
                if (i == 0 && isFirst) {//图片新闻模式JSON
                    JSONObject jsonStrObj = (JSONObject) jsonArray.opt(i);
                    String pic = jsonStrObj.toString();
                    JSONObject jsonPicObj = new JSONObject(pic);
                    JSONArray jsonPicArray = jsonPicObj.getJSONArray("ads");
                    for (int j = 0; j < jsonPicArray.length(); j++) {
                        JSONObject jsonPicStrObj = (JSONObject) jsonPicArray.opt(j);
                        String title = jsonPicStrObj.getString("title"); //标题
                        String imgsrc = jsonPicStrObj.getString("imgsrc");
                        String newsUrl = PicNews(jsonPicStrObj.getString("url"));
                        HashMap<String, Object> listm = new HashMap<String, Object>();
                        listm.put("title", title);
                        listm.put("imgsrc", imgsrc);
                        adList.add(listm);
                        WebAddress.picNews[j] = newsUrl;
                    }
                } else { //消息item模式JSON
                    JSONObject jsonStrObj = (JSONObject) jsonArray.opt(i);
                    if (jsonStrObj.has("boardid")) {
                        String title = jsonStrObj.getString("title"); //标题
                        String imgsrc = jsonStrObj.getString("imgsrc");
                        String replyCount = jsonStrObj.getString("replyCount");
                        String digest = jsonStrObj.getString("digest");//描述
                        String boardid = jsonStrObj.getString("boardid");//类型
                        String newsUrl = "";

                        if ("".equals(digest)) {//判断是否为多图模式JSON
                            String img = jsonStrObj.toString();
                            JSONObject jsonImgObj = new JSONObject(img);
                            JSONArray jsonImgArray = jsonImgObj.getJSONArray("imgextra");
                            HashMap<String, Object> listm = new HashMap<String, Object>();
                            for (int j = 0; j < jsonImgArray.length(); j++) {
                                JSONObject jsonImgStrObj = (JSONObject) jsonImgArray.opt(j);
                                String mImgsrc = "imgsrc" + j + "";
                                String Img = jsonImgStrObj.getString("imgsrc"); //标题
                                listm.put(mImgsrc, Img);
                            }
                            newsUrl = threePicNews(jsonStrObj.getString("skipID"));
                            listm.put("newsURL", newsUrl);
                            listm.put("title", title);
                            listm.put("digest", digest);
                            listm.put("imgsrc", imgsrc);
                            listm.put("newsURL", newsUrl);
                            listm.put("replyCount", replyCount);
                            list.add(listm);
                        } else if ("video_bbs".equals(boardid)) {
                            HashMap<String, Object> listm = new HashMap<String, Object>();
                            newsUrl = video(jsonStrObj.getString("videoID"));
                            listm.put("newsURL", newsUrl);
                            listm.put("title", title);
                            listm.put("digest", digest);
                            listm.put("imgsrc", imgsrc);
                            listm.put("newsURL", newsUrl);
                            listm.put("replyCount", replyCount);
                            list.add(listm);
                        } else { //单图模式
                            HashMap<String, Object> listm = new HashMap<String, Object>();
                            newsUrl = jsonStrObj.getString("url_3w");
                            listm.put("newsURL", newsUrl);
                            listm.put("title", title);
                            listm.put("digest", digest);
                            listm.put("imgsrc", imgsrc);
                            listm.put("newsURL", newsUrl);
                            listm.put("replyCount", replyCount);
                            list.add(listm);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            System.out.println("Jsons parse error !");
            e.printStackTrace();
        }
    }

    //轮播图片的URL  抓取"url"
    private static String PicNews(String url) {
        String picStr;
        url = url.replace("|", "/");//把str字符串中所有的字符|都替换成字符/
        picStr = "http://news.163.com/photoview/" + url + ".html#p=BN8F0F543R710001";
        return picStr;
    }

    //单图模式的URL   抓取"postid"
    private static String sigPicNews(String postid) {
        postid = "http://c.m.163.com/nc/article/" + postid + "/full.html";
        return postid;
    }

    //三图模式的URL  抓取"skipID"  skipID："00AP0001|118678"
    private static String threePicNews(String skipID) {
        String str = skipID.split("\\|")[1];//他会把按|分开的部分存储到数组中 str[0] 中的内容就是00AP0001  str[1]中的内容就是118678
        String channel = skipID.substring(4, 8);
        String picStr = "http://3g.163.com/ntes/special/0034073A/photoshare.html?setid=" + str + "&channelid=" + channel + "&spst=3&spss=newsapp&spsf=qq&spsw=1";
        return picStr;
    }

    //视频模式的URL   抓取"videoID"
    private static String video(String videoID) {
        videoID = "http://3g.163.com/ntes/special/0034073A/wechat_article.html?spst=0&spss=newsapp&spsw=1&spsf=qq&videoid=" + videoID + "&token=null";
        return videoID;
    }

}
