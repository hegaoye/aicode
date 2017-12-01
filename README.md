<H4>项目官网</H4></br>
www.ponddy.com </br>
www.ponddytutors.com </br>

项目为庞帝的在线教育系统</br>
两大块:</br>
<span style="color:green;font-size:20px;">1.预约课程，上课的业务</span></br>
<span style="color:green;font-size:20px;">2.购买课时，分享课时业务</span></br>

<H4>代码规范说明</H4>

1.严格遵守java代码开发规范，驼峰命名法 例如：   xxxXXX  xxx_xxxx  XXXDDDD

2.接口类命名XXXSV  接口实现命名XXXXSVImpl

3.控制器类命名XXXCtrl 

4.注释说明：</br>

接口定义注释范例 </br>

    /**
     * 添加用户到组
     * 1.判断参数
     * 2.判断组是否有上级
     * 3.封装数据
     * 4.记录日志
     * 5.返回结果
     *
     * @param user_id     用户id
     * @param user_grp_id 组id
     * @param enable      是否启用 0 否 1是
     * @return BeanServerReturn
     * @throws UserGroupException
     */
    BeanServerReturn addUserToGroup(Long user_id, Long user_grp_id, int enable) throws UserGroupException;

    /**
      * 添加用户到组
      * 1.判断参数
      * 2.判断组是否有上级
      * 3.封装数据
      * 4.记录日志
      * 5.返回结果
      *
      * @param user_id     用户id
      * @param user_grp_id 组id
      * @param enable      是否启用 0 否 1是
      * @return BeanServerReturn
      * @throws UserGroupException
      */
    @Override
    public BeanServerReturn addUserToGroup(Long user_id, Long user_grp_id, int enable) throws UserGroupException {
        //1.判断参数
        if (user_id == null || user_id == 0 || user_grp_id == null || user_grp_id == 0)
            throw new UserGroupException("缺少参数错误！");
        BeanServerReturn bsr = new BeanServerReturn();
        //2.判断组是否有上级
        Map<String, Object> param = Maps.newHashMap();
        param.put("userGrpId", user_grp_id);
        RbacUserUserGroup rbacUserUserGroup = rbacUserUserGroupDao.load(param);
        //3.封装数据
        if (rbacUserUserGroup != null) {
            rbacUserUserGroup.setId(null);
        } else rbacUserUserGroup = new RbacUserUserGroup();
        rbacUserUserGroup.setUserId(user_id);
        rbacUserUserGroup.setUserGrpId(user_grp_id);
        rbacUserUserGroup.setUtcCreateTime(new Date());
        rbacUserUserGroup.setUpdateTime(new Date());
        rbacUserUserGroup.setEnable(enable);
        rbacUserUserGroupDao.insert(rbacUserUserGroup);
        if (rbacUserUserGroup.getId() > 0) {
            bsr.setSuccess(true);
            bsr.setInfo("加入组成功！");
        }
        //TODO 4.记录日志
        //5.返回结果
        return bsr;
    }

    注意注释中的1，2，3，4，5的代码实现步骤描述非常重要,并且代码编写过程中要把注释引入到方法体中按照步骤进行逐步实现,
    同时注意@param的变量注释



    控制器层注释规范范例


    /**
      * 添加新的用户组
      * 1.判断参数
      * 2.数据封装
      * 3.保存数据并记录日志
      * 4.返回保存结果
      *
      * @param name   用户组名
      * @param en     是否可用 0否 1是
      * @param remark 备注
      * @return
      */
    @RequestMapping("/addusergroup")
    @ResponseBody
    public BeanServerReturn addusergroup(String name, int en, String remark) {
        BeanServerReturn bsr = new BeanServerReturn();

        try {
            //1.判断参数
            if (StringUtils.isBlank(name)) throw new UserGroupException("用户组名称为空异常！");
            //2.数据封装
            RbacUserGroup rbacUserGroup = new RbacUserGroup();
            rbacUserGroup.setName(name);
            rbacUserGroup.setEnable(en);
            rbacUserGroup.setRemark(remark);
            rbacUserGroup.setUtcCreateTime(new Date());
            rbacUserGroup.setUpdateTime(new Date());
            //3.保存数据
            userGroupManagerSV.saveOrUpdate(rbacUserGroup);
            logSV.save(Std.LogTB.GROUP.key, CharFunction.toJSONString(rbacUserGroup) + Std.LogType.ADD);
            //4.返回保存结果
            bsr.setSuccess(true);
            bsr.setInfo("保存成功！");
        } catch (BaseException e) {
            e.printStackTrace();
            bsr.setInfo(e.getMessage());
        }
        return bsr;
    }


	 注意注释中的1，2，3，4，5的代码实现步骤描述非常重要,并且代码编写过程中要把注释引入到方法体中按照步骤进行逐步实现,
	 同时注意@param的变量注释

5.凡是状态，变量影响到过程的，不能写死，需要统一声明，并注释清晰，进行分类管理。

6.上传与业务分离，通过独立上传服务sys-base-web完成上传业务