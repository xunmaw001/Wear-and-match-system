
package com.controller;

import java.io.File;
import java.math.BigDecimal;
import java.net.URL;
import java.text.SimpleDateFormat;
import com.alibaba.fastjson.JSONObject;
import java.util.*;
import org.springframework.beans.BeanUtils;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.context.ContextLoader;
import javax.servlet.ServletContext;
import com.service.TokenService;
import com.utils.*;
import java.lang.reflect.InvocationTargetException;

import com.service.DictionaryService;
import org.apache.commons.lang3.StringUtils;
import com.annotation.IgnoreAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.entity.*;
import com.entity.view.*;
import com.service.*;
import com.utils.PageUtils;
import com.utils.R;
import com.alibaba.fastjson.*;

/**
 * 穿搭讲堂
 * 后端接口
 * @author
 * @email
*/
@RestController
@Controller
@RequestMapping("/chuandajiangtang")
public class ChuandajiangtangController {
    private static final Logger logger = LoggerFactory.getLogger(ChuandajiangtangController.class);

    @Autowired
    private ChuandajiangtangService chuandajiangtangService;


    @Autowired
    private TokenService tokenService;
    @Autowired
    private DictionaryService dictionaryService;

    //级联表service

    @Autowired
    private YonghuService yonghuService;


    /**
    * 后端列表
    */
    @RequestMapping("/page")
    public R page(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("page方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));
        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永不会进入");
        else if("用户".equals(role))
            params.put("yonghuId",request.getSession().getAttribute("userId"));
        params.put("chuandajiangtangDeleteStart",1);params.put("chuandajiangtangDeleteEnd",1);
        if(params.get("orderBy")==null || params.get("orderBy")==""){
            params.put("orderBy","id");
        }
        PageUtils page = chuandajiangtangService.queryPage(params);

        //字典表数据转换
        List<ChuandajiangtangView> list =(List<ChuandajiangtangView>)page.getList();
        for(ChuandajiangtangView c:list){
            //修改对应字典表字段
            dictionaryService.dictionaryConvert(c, request);
        }
        return R.ok().put("data", page);
    }

    /**
    * 后端详情
    */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("info方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        ChuandajiangtangEntity chuandajiangtang = chuandajiangtangService.selectById(id);
        if(chuandajiangtang !=null){
            //entity转view
            ChuandajiangtangView view = new ChuandajiangtangView();
            BeanUtils.copyProperties( chuandajiangtang , view );//把实体数据重构到view中

            //修改对应字典表字段
            dictionaryService.dictionaryConvert(view, request);
            return R.ok().put("data", view);
        }else {
            return R.error(511,"查不到数据");
        }

    }

    /**
    * 后端保存
    */
    @RequestMapping("/save")
    public R save(@RequestBody ChuandajiangtangEntity chuandajiangtang, HttpServletRequest request){
        logger.debug("save方法:,,Controller:{},,chuandajiangtang:{}",this.getClass().getName(),chuandajiangtang.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
        if(false)
            return R.error(511,"永远不会进入");

        Wrapper<ChuandajiangtangEntity> queryWrapper = new EntityWrapper<ChuandajiangtangEntity>()
            .eq("chuandajiangtang_name", chuandajiangtang.getChuandajiangtangName())
            .eq("chuandajiangtang_video", chuandajiangtang.getChuandajiangtangVideo())
            .eq("chuandajiangtang_types", chuandajiangtang.getChuandajiangtangTypes())
            .eq("chuandajiangtang_clicknum", chuandajiangtang.getChuandajiangtangClicknum())
            .eq("shangxia_types", chuandajiangtang.getShangxiaTypes())
            .eq("chuandajiangtang_delete", chuandajiangtang.getChuandajiangtangDelete())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ChuandajiangtangEntity chuandajiangtangEntity = chuandajiangtangService.selectOne(queryWrapper);
        if(chuandajiangtangEntity==null){
            chuandajiangtang.setChuandajiangtangClicknum(1);
            chuandajiangtang.setShangxiaTypes(1);
            chuandajiangtang.setChuandajiangtangDelete(1);
            chuandajiangtang.setCreateTime(new Date());
            chuandajiangtangService.insert(chuandajiangtang);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 后端修改
    */
    @RequestMapping("/update")
    public R update(@RequestBody ChuandajiangtangEntity chuandajiangtang, HttpServletRequest request){
        logger.debug("update方法:,,Controller:{},,chuandajiangtang:{}",this.getClass().getName(),chuandajiangtang.toString());

        String role = String.valueOf(request.getSession().getAttribute("role"));
//        if(false)
//            return R.error(511,"永远不会进入");
        //根据字段查询是否有相同数据
        Wrapper<ChuandajiangtangEntity> queryWrapper = new EntityWrapper<ChuandajiangtangEntity>()
            .notIn("id",chuandajiangtang.getId())
            .andNew()
            .eq("chuandajiangtang_name", chuandajiangtang.getChuandajiangtangName())
            .eq("chuandajiangtang_video", chuandajiangtang.getChuandajiangtangVideo())
            .eq("chuandajiangtang_types", chuandajiangtang.getChuandajiangtangTypes())
            .eq("chuandajiangtang_clicknum", chuandajiangtang.getChuandajiangtangClicknum())
            .eq("shangxia_types", chuandajiangtang.getShangxiaTypes())
            .eq("chuandajiangtang_delete", chuandajiangtang.getChuandajiangtangDelete())
            ;

        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ChuandajiangtangEntity chuandajiangtangEntity = chuandajiangtangService.selectOne(queryWrapper);
        if("".equals(chuandajiangtang.getChuandajiangtangPhoto()) || "null".equals(chuandajiangtang.getChuandajiangtangPhoto())){
                chuandajiangtang.setChuandajiangtangPhoto(null);
        }
        if("".equals(chuandajiangtang.getChuandajiangtangVideo()) || "null".equals(chuandajiangtang.getChuandajiangtangVideo())){
                chuandajiangtang.setChuandajiangtangVideo(null);
        }
        if(chuandajiangtangEntity==null){
            chuandajiangtangService.updateById(chuandajiangtang);//根据id更新
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }

    /**
    * 删除
    */
    @RequestMapping("/delete")
    public R delete(@RequestBody Integer[] ids){
        logger.debug("delete:,,Controller:{},,ids:{}",this.getClass().getName(),ids.toString());
        ArrayList<ChuandajiangtangEntity> list = new ArrayList<>();
        for(Integer id:ids){
            ChuandajiangtangEntity chuandajiangtangEntity = new ChuandajiangtangEntity();
            chuandajiangtangEntity.setId(id);
            chuandajiangtangEntity.setChuandajiangtangDelete(2);
            list.add(chuandajiangtangEntity);
        }
        if(list != null && list.size() >0){
            chuandajiangtangService.updateBatchById(list);
        }
        return R.ok();
    }


    /**
     * 批量上传
     */
    @RequestMapping("/batchInsert")
    public R save( String fileName){
        logger.debug("batchInsert方法:,,Controller:{},,fileName:{}",this.getClass().getName(),fileName);
        try {
            List<ChuandajiangtangEntity> chuandajiangtangList = new ArrayList<>();//上传的东西
            Map<String, List<String>> seachFields= new HashMap<>();//要查询的字段
            Date date = new Date();
            int lastIndexOf = fileName.lastIndexOf(".");
            if(lastIndexOf == -1){
                return R.error(511,"该文件没有后缀");
            }else{
                String suffix = fileName.substring(lastIndexOf);
                if(!".xls".equals(suffix)){
                    return R.error(511,"只支持后缀为xls的excel文件");
                }else{
                    URL resource = this.getClass().getClassLoader().getResource("static/upload/" + fileName);//获取文件路径
                    File file = new File(resource.getFile());
                    if(!file.exists()){
                        return R.error(511,"找不到上传文件，请联系管理员");
                    }else{
                        List<List<String>> dataList = PoiUtil.poiImport(file.getPath());//读取xls文件
                        dataList.remove(0);//删除第一行，因为第一行是提示
                        for(List<String> data:dataList){
                            //循环
                            ChuandajiangtangEntity chuandajiangtangEntity = new ChuandajiangtangEntity();
//                            chuandajiangtangEntity.setChuandajiangtangName(data.get(0));                    //讲堂名称 要改的
//                            chuandajiangtangEntity.setChuandajiangtangPhoto("");//照片
//                            chuandajiangtangEntity.setChuandajiangtangVideo(data.get(0));                    //讲堂视频 要改的
//                            chuandajiangtangEntity.setChuandajiangtangTypes(Integer.valueOf(data.get(0)));   //讲堂类型 要改的
//                            chuandajiangtangEntity.setChuandajiangtangClicknum(Integer.valueOf(data.get(0)));   //点击次数 要改的
//                            chuandajiangtangEntity.setShangxiaTypes(Integer.valueOf(data.get(0)));   //是否上架 要改的
//                            chuandajiangtangEntity.setChuandajiangtangDelete(1);//逻辑删除字段
//                            chuandajiangtangEntity.setChuandajiangtangContent("");//照片
//                            chuandajiangtangEntity.setCreateTime(date);//时间
                            chuandajiangtangList.add(chuandajiangtangEntity);


                            //把要查询是否重复的字段放入map中
                        }

                        //查询是否重复
                        chuandajiangtangService.insertBatch(chuandajiangtangList);
                        return R.ok();
                    }
                }
            }
        }catch (Exception e){
            return R.error(511,"批量插入数据异常，请联系管理员");
        }
    }





    /**
    * 前端列表
    */
    @IgnoreAuth
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params, HttpServletRequest request){
        logger.debug("list方法:,,Controller:{},,params:{}",this.getClass().getName(),JSONObject.toJSONString(params));

        // 没有指定排序字段就默认id倒序
        if(StringUtil.isEmpty(String.valueOf(params.get("orderBy")))){
            params.put("orderBy","id");
        }
        PageUtils page = chuandajiangtangService.queryPage(params);

        //字典表数据转换
        List<ChuandajiangtangView> list =(List<ChuandajiangtangView>)page.getList();
        for(ChuandajiangtangView c:list)
            dictionaryService.dictionaryConvert(c, request); //修改对应字典表字段
        return R.ok().put("data", page);
    }

    /**
    * 前端详情
    */
    @RequestMapping("/detail/{id}")
    public R detail(@PathVariable("id") Long id, HttpServletRequest request){
        logger.debug("detail方法:,,Controller:{},,id:{}",this.getClass().getName(),id);
        ChuandajiangtangEntity chuandajiangtang = chuandajiangtangService.selectById(id);
            if(chuandajiangtang !=null){

                //点击数量加1
                chuandajiangtang.setChuandajiangtangClicknum(chuandajiangtang.getChuandajiangtangClicknum()+1);
                chuandajiangtangService.updateById(chuandajiangtang);

                //entity转view
                ChuandajiangtangView view = new ChuandajiangtangView();
                BeanUtils.copyProperties( chuandajiangtang , view );//把实体数据重构到view中

                //修改对应字典表字段
                dictionaryService.dictionaryConvert(view, request);
                return R.ok().put("data", view);
            }else {
                return R.error(511,"查不到数据");
            }
    }


    /**
    * 前端保存
    */
    @RequestMapping("/add")
    public R add(@RequestBody ChuandajiangtangEntity chuandajiangtang, HttpServletRequest request){
        logger.debug("add方法:,,Controller:{},,chuandajiangtang:{}",this.getClass().getName(),chuandajiangtang.toString());
        Wrapper<ChuandajiangtangEntity> queryWrapper = new EntityWrapper<ChuandajiangtangEntity>()
            .eq("chuandajiangtang_name", chuandajiangtang.getChuandajiangtangName())
            .eq("chuandajiangtang_video", chuandajiangtang.getChuandajiangtangVideo())
            .eq("chuandajiangtang_types", chuandajiangtang.getChuandajiangtangTypes())
            .eq("chuandajiangtang_clicknum", chuandajiangtang.getChuandajiangtangClicknum())
            .eq("shangxia_types", chuandajiangtang.getShangxiaTypes())
            .eq("chuandajiangtang_delete", chuandajiangtang.getChuandajiangtangDelete())
            ;
        logger.info("sql语句:"+queryWrapper.getSqlSegment());
        ChuandajiangtangEntity chuandajiangtangEntity = chuandajiangtangService.selectOne(queryWrapper);
        if(chuandajiangtangEntity==null){
            chuandajiangtang.setChuandajiangtangDelete(1);
            chuandajiangtang.setCreateTime(new Date());
        chuandajiangtangService.insert(chuandajiangtang);
            return R.ok();
        }else {
            return R.error(511,"表中有相同数据");
        }
    }


}
