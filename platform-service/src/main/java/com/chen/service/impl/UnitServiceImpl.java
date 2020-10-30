package com.chen.service.impl;

import com.chen.core.base.Constant;
import com.chen.dao.UnitMapper;
import com.chen.entity.Unit;
import com.chen.entity.ZTree;
import com.chen.service.IUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UnitServiceImpl implements IUnitService {

    @Autowired
    private UnitMapper unitMapper;

    /**
     * 根据单位ID查找unit对象
     * @param unitId 单位Id
     * @return 单位对象
     */
    @Override
    public Unit findUnitById(String unitId) {
        return unitMapper.findUnitById(unitId);
    }

    @Override
    public List<Unit> findAllUnitByParentUnitId(String unitId) {

        return null;
    }

    @Override
    public List<ZTree> findUnitMenu(String unitId) {
        //菜单list
        List<Unit> menu = new ArrayList<>();
        //根据unit获取根单位
        Unit rootUnit = unitMapper.findUnitById(unitId);
        //根单位的所有子单位
        List<Unit> UnitMenus = new ArrayList<>();

        UnitMenus.add(rootUnit);
        //递归组织成菜单list
        getMenuList(UnitMenus,menu);
        //将菜单的内容整合到一个与前台zTree对应的类的集合中,为了与ZTree的json格式对应
        List<ZTree> treeList = new ArrayList<ZTree>();
        for (Unit unitMenu : menu) {
            ZTree ztree = new ZTree();
            ztree.setId(unitMenu.getUnit_id());
            ztree.setName(unitMenu.getUnit_name());
            ztree.setpId(unitMenu.getParent_unit_id());
            //点击节点执行的语句
            ztree.setClick("javaScript:onclickNode('"+unitMenu.getUnit_id()+"');");
            if(unitMenu.getLevel() == 0){
                ztree.setOpen(true);
            }else{
                ztree.setOpen(false);
            }
            treeList.add(ztree);
        }
        return treeList;
    }
    /**
     *将查询到单位对象，递归组织成菜单list
     * @param unitMenus 单位对象
     */
    private void getMenuList(List<Unit> unitMenus, List<Unit> menu) {
        for (Unit nav : unitMenus) {
            menu.add(nav);
            //如果此单位拥有子单位
            if(nav.getMenus()!=null){
                getMenuList(nav.getMenus(),menu);
            }
        }
    }

    /**
     * 获取当前单位的一级单位
     * @param parentUnitId 父ID
     * @return 一级单位对象
     */
    public Unit queryCompanyUnitByUserParentUnitId(String parentUnitId) {
        /*
         * 将传入的父id作为单位id查询单位对象,判断其单位对象的层级是否为配置文件中
         * 顶级单位(公司)的层级,若是则返回单位对象,若不是,则递归查询
         */
        Unit unit = unitMapper.findUnitById("025unit1");
        return unit;
    }

    @Override
    public List<Unit> findUnitList() {
        return unitMapper.findUnitList();
    }

    @Override
    public Unit findUnitByName(String unitName) {
        return unitMapper.findUnitByName(unitName);
    }
}
