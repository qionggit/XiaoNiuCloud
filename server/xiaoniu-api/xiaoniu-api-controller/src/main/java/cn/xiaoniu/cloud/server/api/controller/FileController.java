package cn.xiaoniu.cloud.server.api.controller;

import cn.xiaoniu.cloud.server.api.model.vo.FileSliceUploadVO;
import cn.xiaoniu.cloud.server.api.model.vo.FileUploadVO;
import cn.xiaoniu.cloud.server.service.FileService;
import cn.xiaoniu.cloud.server.web.authority.Login;
import cn.xiaoniu.cloud.server.web.response.Result;
import cn.xiaoniu.cloud.server.web.response.ResultStatus;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文件操作接口
 *
 * @author 孔明
 * @date 2020/4/26 11:36
 * @description cn.xiaoniu.cloud.server.api.controller.FileController
 */
@Api(tags = "文件操作接口")
@RestController("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * 通过MD5判断是否上传完成
     *
     * @param md5  MD5
     * @param size 文件大小
     * @return
     */
    @Login
    @GetMapping("/isUpload")
    @ApiOperation("查询文件是否上传")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "AccessToken", value = "登录令牌", required = true, paramType = "header"),
            @ApiImplicitParam(name = "md5", value = "文件MD5值", required = true, paramType = "query"),
            @ApiImplicitParam(name = "size", value = "文件MD5大小", required = true, paramType = "query")
    })
    public Result<FileUploadVO> isUpload(@RequestParam("md5") String md5,
                                         @RequestParam("size") Long size) {
        if (size <= 0) {
            return Result.fail(ResultStatus.ERROR_REQUEST, "文件大小应该大于0！");
        }
        return fileService.isUpload(md5, size);
    }

    /**
     * 准备上传
     *
     * @return cn.xiaoniu.cloud.server.web.response.Result
     * @author 孔明
     * @date 2020-05-12 16:07:40
     */
    @Login
    @GetMapping("/readyUpload")
    @ApiOperation("准备上传")
    @ApiImplicitParams(value = {
            @ApiImplicitParam(name = "AccessToken", value = "登录令牌", required = true, paramType = "header"),
            @ApiImplicitParam(name = "md5", value = "文件MD5值", required = true, paramType = "query"),
            @ApiImplicitParam(name = "size", value = "文件MD5大小", required = true, paramType = "query"),
            @ApiImplicitParam(name = "name", value = "文件名称", required = true, paramType = "query"),
            @ApiImplicitParam(name = "fileType", value = "文件类型（10:图片；20:视频；30:文档；40:音乐；50:种子；60:其他）", required = true, paramType = "query")
    })
    public Result<FileSliceUploadVO> readyUpload(@RequestParam("directoryId") Long directoryId,
                                                 @RequestParam("md5") String md5,
                                                 @RequestParam("size") Long size,
                                                 @RequestParam("name") String name,
                                                 @RequestParam("fileType") Integer fileType) {
        if (size <= 0) {
            return Result.fail(ResultStatus.ERROR_REQUEST, "文件大小应该大于0！");
        }
        return fileService.readyUpload(directoryId, md5, size, name, fileType);
    }

}
