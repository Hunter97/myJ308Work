/*
 * Paul Wallace
 * 90.308 Assignment 2
 * 
 */
package test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import main.DuplicateFiles;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import static org.mockito.Mockito.*;

/**
 *
 * @author prwallace
 */
public class DuplicateFilesTest {
    private File m_file;

    private Path m_path;
    private Path m_mockedPath;
    private Path m_invalidPath;
    private List< File > m_resultDirList = null;
    private List< File > m_resultFileList = null;
    private List< File > m_mockFileList = null;

    private DuplicateFiles m_dupFiles;
    private DuplicateFiles m_mockDupFiles;

    private String m_testDirPath = "C:\\temp";
    private String m_testFilePath = "C:\\temp\\build.xlm";
    private String m_mappedDrive = "Z:\\";

    private int m_numOfFilesInDir = 24;
    private int m_numOfDupFiles = 11;
    private int m_numOfDir = 10;


    @Before
    public void setUpClass() {
        m_mockDupFiles = mock( DuplicateFiles.class );
        m_mockFileList = mock( List.class );

        m_path = Paths.get( m_testDirPath );
        m_file = new File( m_testDirPath  );
        m_dupFiles = new DuplicateFiles();
        m_mockedPath = mock( Path.class );
        m_invalidPath = Paths.get( m_testFilePath );
    }


    /**
     * Create a TemporaryFolder object, to test hasFiles method
     */
    @Rule
    public TemporaryFolder tmpDir = new TemporaryFolder();

    /**
     * Create a rule for testing exceptions.
     */
    @Rule
    public ExpectedException thrown= ExpectedException.none();


    /**
     * Test the hasFiles method.
     * Test for:
     *      * Return false when sent a Path reference to a file
     *      * Returns false when sent a Path reference to an empty directory
     *      * Returns true when sent a Path reference to a sub directory
     *      * Returns true when sent a Path reference to test directory, m_path
     */
    @Test
    public void testHasFiles() {    
        boolean result = false;
        boolean expect = false;
        File emptyFolder = tmpDir.newFolder( "tmp" );

        try {
            /*
             * Verify hasFiles returns false when passed a Path to a file
             * Mock DuplicateFiles & Path Class; return false when hasFiles is called
             * Pass hasFiles a Path reference to a real file
             * Pass test if both conditions return false
             * 
             */
            when( m_mockDupFiles.hasFiles( m_mockedPath )).thenReturn( false );
            expect = m_mockDupFiles.hasFiles( m_mockedPath );
            result = m_dupFiles.hasFiles( m_invalidPath );
            assertEquals( "testHasFiles(file not dir) ", expect, result );


            /**
             * Verify hasFiles returns false when passed an empty directory
             * Create an empty TemporaryFolder
             * Mock DuplicateFiles & Path Class; return false when hasFiles is called
             * Pass hasFiles the empty TemporaryFolder
             * Pass test if both conditions return false
             */
            expect = false;
            result = false;
            when( m_mockDupFiles.hasFiles( m_mockedPath )).thenReturn( false );
            expect = m_mockDupFiles.hasFiles( m_mockedPath ); 
            result = m_dupFiles.hasFiles( emptyFolder.toPath() );
            assertEquals( "testHasFiles(empty dir) ", expect, result );


            /**
             * Verify hasFiles returns true when a file is locating inside a sub
             *      folder of the arguments parent directory.
             * Create another TemporaryFolder inside the previous TemporaryFolder
             *      to make a sub folder ".\tmp\subDir".
             * Create a temp file and place it into the sub folder
             * Mock DuplicateFiles & Path Class; return true when hasFiles is called
             * Pass hasFiles the empty parent folder ".\tmp".
             * Pass test if both conditions return true
             */
            File subDir = tmpDir.newFolder( "tmp\\subDir" );
            File tmpFile = tmpDir.newFile( "tmp\\subDir\\tmpFile" );
            when( m_mockDupFiles.hasFiles( m_mockedPath )).thenReturn( true );
            expect = m_mockDupFiles.hasFiles( m_mockedPath ); 
            result = m_dupFiles.hasFiles( subDir.toPath() );
            assertEquals( "testHasFiles(file in sub dir) ", expect, result );


            /**
             * Verify passing hasFiles the parent test directory, m_path, returns true
             * Pass test if method returns true
             */
            result = false;
            result = m_dupFiles.hasFiles( m_path );
            assertTrue( "testHasFiles(main dir) ", result );
        }
        catch( IOException e ) {
            fail( "IOExceptioin occured while running testHasFiles" );
        }
        catch( NullPointerException e ) {
            fail( "NullPointerException occured while running testHasFiles" );
        }
    }



    /**
     * Test the createDirectoryFileList() method
     * Test for:
     *      * Size of returned List< Path > is same size as File[] created from
     *          same parent directory.
     *      * List< Path > has identical contents of File[] of previous test
     */
    @Test
    public void testCreateDirectoryFileLists() throws Exception {
        m_dupFiles.createDirectoryFileLists( m_path );
        m_resultDirList = m_dupFiles.getDirList();
        boolean isEqual = false;

        /*
         * Verify the size of the List< File > returned by getDirList is 10
         */
        assertEquals( "getDirectoryList(check size) ", m_numOfDir, m_resultDirList.size() );


        /*
         * Verify a new instance of DuplicateFiles returns the same number of 
         *      directories for the same given Path.
         * Compare the results against a mock instance of DuplicateFiles that
         * Pass test if both files are equal
         */
        DuplicateFiles testDupFiles = new DuplicateFiles();
        when( m_mockDupFiles.getDirList()).thenReturn( m_resultDirList );
        m_mockFileList = m_mockDupFiles.getDirList();
        testDupFiles.createDirectoryFileLists( m_path );
        List< File > tstDirList = testDupFiles.getDirList();
        
        for( int i = 1; i < tstDirList.size(); i++ ) {
            if( !m_mockFileList.get(i).equals( tstDirList.get( i ) )) {
                isEqual = false;
                break;
            }
            else {
                isEqual = true;
            }
        }

        assertTrue( "getFileList(assertTrue) ", isEqual );

        /*
         * Verify the size of the List< File > returned by getFileList is 24
         */
        assertEquals( "createFileList(check size) ", m_numOfFilesInDir, m_dupFiles.getFileList().size() );
    }


    /**
     *  Test createDirectoryFileList throws a NoSuchFileExcepton
     *  Test for:
     *      * A NoSuchFileException
     *      * Sends the method a drive letter that is not present on my PC (Z:\) 
     */
    @Test
    public void throwsNoSuchFileException() throws IOException {
        thrown.expect( java.nio.file.NoSuchFileException.class );
        thrown.expectMessage( m_mappedDrive );
        Path testPath = Paths.get( m_mappedDrive );        
        DuplicateFiles dup = new DuplicateFiles();
        dup.createDirectoryFileLists( testPath );
    }


    /**
     * Test duplicateFiles() method
     * Test for:
     *      * The number of duplicate files equals 11
     */
    @Test
    public void testDuplicateFiles() {
        List< File > dupFiles;
        try {
            m_dupFiles.createDirectoryFileLists( m_path );
            m_resultDirList = m_dupFiles.getDirList();
            m_resultFileList = m_dupFiles.getFileList();
            dupFiles = m_dupFiles.getDuplicateFiles();
            assertEquals( "duplicateFiles(check size) ", m_numOfDupFiles, dupFiles.size() );
        }
        catch( IOException e ){
            fail( "IOException occured in testDuplicateFiles" );
        }
        catch( NullPointerException e ) {
            fail( "NullPointerException occured in testDuplicateFiles" );
        }
    }
}