/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package cloud.cleo.chimesma.cdk.resources;

import java.util.List;
import software.amazon.awscdk.RemovalPolicy;
import software.amazon.awscdk.services.iam.Effect;
import software.amazon.awscdk.services.iam.PolicyDocument;
import software.amazon.awscdk.services.iam.PolicyStatement;
import software.amazon.awscdk.services.iam.ServicePrincipal;
import software.amazon.awscdk.services.s3.Bucket;
import software.amazon.awscdk.services.s3.BucketProps;
import software.amazon.awscdk.services.s3.CfnBucketPolicy;
import software.amazon.awscdk.services.s3.CfnBucketPolicyProps;
import software.constructs.Construct;

/**
 * S3 Bucket that adds permissions for Chime SDK to play prompts stored in Bucket
 * 
 * @author sjensen
 */
public class ChimeS3PromptBucket extends Bucket {
    
    public ChimeS3PromptBucket(Construct scope, String id, BucketProps props) {
        super(scope, "PromptBucket", BucketProps.builder()
                .removalPolicy(RemovalPolicy.DESTROY)
                .build());

        new CfnBucketPolicy(this, "PromptBucketPolicy", CfnBucketPolicyProps.builder()
                .bucket(getBucketName())
                .policyDocument(PolicyDocument.Builder.create()
                        .statements(List.of(PolicyStatement.Builder.create()
                                .effect(Effect.ALLOW)
                                .principals(List.of(new ServicePrincipal("voiceconnector.chime.amazonaws.com")))
                                .actions(List.of("s3:GetObject"))
                                .resources(List.of(getBucketArn(), getBucketArn() + "/*"))
                                .build()))
                        .build())
                .build());
    }
    
}
