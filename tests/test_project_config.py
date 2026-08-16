import unittest
import os

class TestProjectConfig(unittest.TestCase):

    def test_manifest_exists(self):
        """Verify that AndroidManifest.xml exists and has leanback launcher category."""
        manifest_path = os.path.join("app", "src", "main", "AndroidManifest.xml")
        self.assertTrue(os.path.exists(manifest_path), "AndroidManifest.xml must exist")
        
        with open(manifest_path, "r", encoding="utf-8") as f:
            content = f.read()
        self.assertIn("category.LEANBACK_LAUNCHER", content)
        self.assertIn("category.HOME", content)


    def test_hardware_profile_constraints(self):
        """Verify hardware profile guidelines for low-spec TV (Mi TV 4A 1GB RAM) from AGENTS.md."""
        agents_path = "AGENTS.md"
        self.assertTrue(os.path.exists(agents_path), "AGENTS.md must exist")
        
        with open(agents_path, "r", encoding="utf-8") as f:
            content = f.read()
        self.assertIn("Xiaomi Mi TV 4A", content)
        self.assertIn("44dp", content)

    def test_package_id(self):
        """Verify package namespace consistency in gradle build configuration."""
        build_gradle = os.path.join("app", "build.gradle.kts")
        self.assertTrue(os.path.exists(build_gradle))
        
        with open(build_gradle, "r", encoding="utf-8") as f:
            content = f.read()
        self.assertIn('namespace = "com.ekshana.tv.launcher"', content)

if __name__ == "__main__":
    unittest.main()
