package com.religion.zhiyun.file.entity;

import lombok.*;

import javax.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "RM_FILE_INFO")
public class FileEntity implements Serializable {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private int fileId;//主键ID

    @Column(name = "FILE_NAME")
    private String fileName;//中文名称

    @Column(name = "FILE_PATH")
    private String filePath;//中文名称

    @Column(name = "FILE_TYPE")
    private String fileType;//中文名称

    @Column(name = "FILE_TITLE")
    private String fileTitle;//中文名称

    @Column(name = "CREATOR")
    private String creator;//创建人

    @Column(name = "CREATE_TIME")
    private Timestamp createTime;//创建时间

}
