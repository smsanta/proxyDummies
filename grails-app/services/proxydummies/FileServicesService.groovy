package proxydummies

import grails.gorm.transactions.Transactional
import proxydummies.abstracts.BaseService

import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.util.concurrent.ExecutorService

@Transactional
class FileServicesService extends BaseService{

    SystemConfigsService systemConfigsService
    ExecutorService executorService

    String loadFileData( String pFilePath ){
        String loadedData = ""
        Path filePath = Paths.get( pFilePath )
        if( Files.exists( filePath ) ){
            byte[] encoded = Files.readAllBytes( filePath )
            loadedData = new String(encoded, Charset.defaultCharset())
        }

        loadedData
    }

    void saveDataIntoFile(String data, String pFilePath, String fileName){
        executorService.execute {
            String fullFilePathString = [pFilePath, fileName].join( getFileSeparator() )
            Path filePath = Paths.get( pFilePath )
            Path fullFilePath = Paths.get( fullFilePathString )
            info( "Saving file $fullFilePathString" )
            if( checkPathExists( filePath ) ){
                Files.deleteIfExists( fullFilePath )

                File.newInstance( fullFilePathString ).with {
                    createNewFile()
                    append( data )
                }
            }
        }
    }

    Boolean checkPathExists( Path path, Boolean createIfNotExist = true){
        Boolean exists = Files.exists( path )
        if( createIfNotExist ){
            Files.createDirectories( path )
            exists = checkPathExists( path, false )
        }

        exists
    }

    Boolean checkPathExists( String pPath, Boolean createIfNotExist = true){
        Path path = Paths.get( pPath )
        checkPathExists(path, createIfNotExist)
    }

    String getUserHomeFolder(){
        System.getProperty( "user.home" )
    }

    String getFileSeparator(){
        System.getProperty( "file.separator" )
    }

    String buildDefaultProxyDummiesHomeFolder(){
        getUserHomeFolder() + getFileSeparator() + "proxyDummies"
    }

    String buildDefaultProxyDummiesSaveResponseFolder(){
        buildDefaultProxyDummiesHomeFolder() + getFileSeparator() + "responses"
    }

}
