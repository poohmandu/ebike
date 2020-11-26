/*
 * Copyright 2020 聂钊 nz@qdigo.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.qdigo.ebike.usercenter.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import com.qdigo.ebike.common.core.domain.R;
import com.qdigo.ebike.common.core.util.http.NetUtil;
import com.qdigo.ebike.usercenter.domain.entity.User;
import com.qdigo.ebike.usercenter.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/v1.0/userInfo")
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class UploadPic {

    private final ResourceLoader resourceLoader;
    private final UserRepository userRepository;
    private final MongoDbFactory mongoDbFactory;
    private final GridFsTemplate gridFsTemplate;

    //@Value("${spring.updir.userPic}")
    public String dir = "upload-dir";

    /**
     * @param mobileNo
     * @param deviceId
     * @param accessToken
     * @param file        上传的头像
     * @return
     */
    @Deprecated
    @PostMapping(value = "/uploadPicture_old")
    public R<?> upload(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestParam("file") MultipartFile file) {

        User user = userRepository.findOneByMobileNo(mobileNo).get();

        if (!file.isEmpty()) {
            String oldFileName = file.getOriginalFilename();
            log.info("\n上传的原文件为:{}", oldFileName);
            String fileName = UUID.randomUUID() + oldFileName.substring(oldFileName.lastIndexOf("."));
            log.info("\n待存储的文件为:" + fileName);

            try {
                String temp = user.getProfileImageId();
                if (temp != null)
                    Files.deleteIfExists(Paths.get(dir, temp.substring(temp.lastIndexOf("/"))));
                Files.copy(file.getInputStream(), Paths.get(dir, fileName));
            } catch (Exception e) {
                e.printStackTrace();
                return R.ok(400, "上传失败:" + e.getMessage());
            }
            String url = NetUtil.getIp() + ":80/v1.0/userInfo/getPic/" + fileName;
            user.setProfileImageId(url);
            userRepository.save(user);

            log.debug("\n返回的头像url为:" + url);
            return R.ok(200, "上传成功", url);
        } else {
            return R.ok(401, "上传文件为空");
        }
    }

    @PostMapping(value = "/uploadPicture")
    public R<?> NewUpload(
            @RequestHeader("mobileNo") String mobileNo,
            @RequestHeader("mobiledeviceId") String deviceId, // 手机设备号
            @RequestHeader("accessToken") String accessToken,
            @RequestParam("file") MultipartFile file) {

        User user = userRepository.findOneByMobileNo(mobileNo).get();
        if (!file.isEmpty()) {
            String oldFileName = file.getOriginalFilename();
            log.debug("上传的原文件为:{}", oldFileName);
            //进行 文件存储
            String url;
            try {
                // 获取文件输入流
                InputStream ins = file.getInputStream();
                // 获取文件类型
                String contentType = file.getContentType();
                // 将文件存储到mongodb中
                ObjectId objectId = gridFsTemplate.store(ins, oldFileName, contentType);
                url = "api.qdigo.net/v1.0/userInfo/getPic/" + objectId;

                log.info("保存成功,地址为:{}", url);

            } catch (IOException e) {
                log.error("保存用户头像失败:", e);
                return R.ok(400, "上传失败:" + e.getMessage());
            }
            user.setProfileImageId(url);
            userRepository.save(user);
            log.debug("返回的头像url为:" + url);
            return R.ok(200, "上传成功", url);
        } else {
            return R.ok(401, "上传文件为空");
        }

    }


    /**
     * @author niezhao
     *
     * @description spring-boot 2 与之前mongodb api不兼容
     *      https://blog.csdn.net/amy126/article/details/88842746
     *
     * @date 2020/1/7 7:48 PM
     * @param filename
     * @return org.springframework.http.ResponseEntity<?>
     *
     */
    @GetMapping(value = "/getPic/{filename:.+}")
    public ResponseEntity<?> getFileNew(@PathVariable("filename") String filename) {
        try {
            Query query = Query.query(Criteria.where("_id").is(filename));
            // 查询单个文件
            GridFSFile gfsFile = gridFsTemplate.findOne(query);

            GridFsResource resource = gridFsTemplate.getResource(gfsFile);
            return ResponseEntity.ok(resource);
        } catch (Exception e) {
            log.error("错误信息" + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping(value = "/getPic_old/{filename:.+}")
    public ResponseEntity<?> getFile(@PathVariable("filename") String filename) {
        try {
            return ResponseEntity.ok(resourceLoader.getResource("file:" + Paths.get(dir, filename)));
        } catch (Exception e) {
            log.error("错误信息" + e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }


}
