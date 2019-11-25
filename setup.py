from setuptools import setup, find_packages

setup(name="TBDBot",
      version="0.1",
      package_dir={'': 'src'},
      packages=find_packages(where='src'),
      entry_points={
        'console_scripts': [
            'TBDBot = tbdbot.tbdbot:main',
        ]
      },
      install_requires=[
        'discord.py>1.2,<2',
        'pandas>0.25,<1',
        'emoji>0.5,<1',
        'psutil>5.6,<6',
      ])

